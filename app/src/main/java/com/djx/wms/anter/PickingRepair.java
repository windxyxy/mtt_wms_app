package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.MyListView;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PickingRepair extends buttom_state {
    private TextView tv_listNOBU, tv_whNOBU, tv_goodNAMEBU, tv_busNOBU, tv_linecodeNOBU, tv_pickedNum;
    private EditText et_whgoodNOBU, et_whcodeNOBU, et_pickNumBU;
    private MyListView mListView;
    private String pickOrder;
    private List<String> listViewData;
    private TextView shuoming;
    private String stock;
    private String wareGoodscode;
    private List<Hashtable> mListHastable;
    private List<Hashtable> mListPos;


    private void init() {
        tv_listNOBU = (TextView) findViewById(R.id.tv_listNOBU);//任务单号
        tv_whNOBU = (TextView) findViewById(R.id.tv_whNOBU);//仓库货品条码tv
        tv_goodNAMEBU = (TextView) findViewById(R.id.tv_goodNAMEBU);//货品名称
        tv_busNOBU = (TextView) findViewById(R.id.tv_busNOBU);//商家编码
        tv_linecodeNOBU = (TextView) findViewById(R.id.tv_linecodeNOBU);//条形码
        tv_pickedNum = (TextView) findViewById(R.id.tv_pickedNum);//已拣数量
        mListView = (MyListView) findViewById(R.id.listview);
        shuoming = (TextView) findViewById(R.id.shuoming);


        et_whgoodNOBU = (EditText) findViewById(R.id.et_whgoodNOBU);//仓库货品条码
        et_whcodeNOBU = (EditText) findViewById(R.id.et_whcodeNOBU);//货位编码
        et_pickNumBU = (EditText) findViewById(R.id.et_pickNumBU);//数量
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_repair);
        init();

        Intent intent = getIntent();
        pickOrder = intent.getStringExtra("pickOrder");
        tv_listNOBU.setText(pickOrder);
        stock = intent.getStringExtra("stock");
        wareGoodscode = intent.getStringExtra("wareGoodscode");

        String sm = "<font color='#ff0000'>注意：建议上架货位列表中，红色标记的货位需要下架原商品后才能重新上架新货品。</font>";
        shuoming.setText(Html.fromHtml(sm));

        String SQL = "select * from v_goods where wareGoodsCodes = '" + wareGoodscode + "'";
        mListHastable = new ArrayList<>();
        mListHastable = Datarequest.GetDataArrayList(SQL);
        if (mListHastable.size() != 0) {
            tv_whNOBU.setText(mListHastable.get(0).get("wareGoodsCodes").toString());//仓库货品条码
            tv_goodNAMEBU.setText(mListHastable.get(0).get("goodsName").toString());//货品名称
            tv_busNOBU.setText(mListHastable.get(0).get("goodsCode").toString());//商家编码
            tv_linecodeNOBU.setText(mListHastable.get(0).get("barCodes").toString());//条形码
            tv_pickedNum.setText(stock);//已拣数量
        }
        Log.e("tv_whNOBU.getText().toString()", "tv_whNOBU.getText().toString()==" + tv_whNOBU.getText().toString());
        //建议货位查询
        String SQL2 = "select dbo.getPosition(c.indexId,2) as pos,d.realStock from t_position as c " +
                " join v_stock as d on d.positionID = c.indexId and d.realStock = 0 " +
//                "and d.wareGoodsCodes = '" + tv_whNOBU.getText().toString() + "' " +
                " where exists (select 1 from t_shelf as b where exists (select 1 from t_warehouse_area as a where a.whAareType = 'JH' and a.warehouseId = " + AppStart.GetInstance().Warehouse + " and a.indexId = b.whAreaId) and b.indexId = c.shelfId)" +
                " union" +
                " select dbo.getPosition(c.indexId,2) as pos,null as realStock from t_position as c " +
                " where exists (select 1 from t_shelf as b where exists (select 1 from t_warehouse_area as a where a.whAareType = 'JH' " +
                " and a.warehouseId = " + AppStart.GetInstance().Warehouse + " and a.indexId = b.whAreaId) and b.indexId = c.shelfId) and not exists (select 1 from t_stock as d where d.positionID = c.indexId)";

        mListPos = new ArrayList<>();
        mListPos = Datarequest.GetDataArrayList(SQL2);

        if (mListPos.size() != 0) {
            listViewData = new ArrayList<>();

            for (int i = -1; i < mListPos.size(); i++) {
                if (i == -1) {
                    listViewData.add("货位编码");
                } else {
                    String pos = mListPos.get(i).get("pos").toString();
                    listViewData.add(pos);
                }
            }

            mListView.setAdapter(new PickingAdapters(this));

        }


        et_whgoodNOBU.setFocusable(true);
        et_whgoodNOBU.setFocusableInTouchMode(true);
        et_whgoodNOBU.requestFocus();

        //货品条码查询回车事件
        et_whgoodNOBU.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    query_whGoodNO();
                }
                return false;
            }


        });
        //货品条码查询失去焦点事件
        et_whgoodNOBU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    query_whGoodNO();
                }
            }
        });
        //货位查询回车事件
        et_whcodeNOBU.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (qurey_whPos()) {
                        return false;
                    } else {
                        et_whcodeNOBU.setError("货位编码不正确");
                        selectall(et_whcodeNOBU);
                        return true;
                    }
                }
                return false;
            }
        });
        //货位查询失去焦点事件
        et_whcodeNOBU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (qurey_whPos()) {

                    } else {
                        et_whcodeNOBU.setError("货位编码不正确");
                        selectall(et_whcodeNOBU);
                        return;
                    }
                }
            }
        });


    }

    //货品条码查询
    private void query_whGoodNO() {
        String goodCode = TransactSQL.instance.filterSQL(et_whgoodNOBU.getText().toString());
        if (goodCode.toString().equals("")) {
            et_whgoodNOBU.setError("货品条码不能为空");
            return;
        }
        if (!goodCode.toString().equals(tv_whNOBU.getText().toString())) {
            et_whgoodNOBU.setError("货品条码错误");
            selectall(et_whgoodNOBU);
            return;
        }
    }

    //货位编码查询
    private boolean qurey_whPos() {
        String pos = TransactSQL.instance.filterSQL(et_whcodeNOBU.getText().toString());
        if (pos.toString().equals("")) {
            return false;
        }
        for (int i = 0; i < mListPos.size(); i++) {
            if (mListPos.get(i).get("pos").toString().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    //补货确认按钮
    public void makeSureBU(View v) {
        if (et_whgoodNOBU.getText().toString().equals("")) {
            et_whgoodNOBU.setError("货品条码不能为空");
            return;
        }
        if (et_whcodeNOBU.getText().toString().equals("")) {
            et_whcodeNOBU.setError("货位编码不能为空");
            return;
        }
        if (et_pickNumBU.getText().toString().equals("")) {
            et_pickNumBU.setError("数量不能为空");
            return;
        }
        if (et_pickNumBU.getText().toString().equals("0")) {
            et_pickNumBU.setError("数量不能为0");
            return;
        }
        int pickNum = Integer.parseInt(et_pickNumBU.getText().toString());
        int pickedNum = Integer.parseInt(tv_pickedNum.getText().toString());
        if (pickNum > pickedNum) {
            et_pickNumBU.setError("补货数量不正确");
            return;
        }
        Hashtable params = new Hashtable<>();
        params.put("SPName", "PRO_MOVESGOODS_HANDHELD_FILL");
        params.put("mgotype", 7);
        params.put("mgoNo", pickOrder);
        String whGoodsCode = TransactSQL.instance.filterSQL(et_whgoodNOBU.getText().toString());
        params.put("wareGoodsCodes", whGoodsCode);
        params.put("realStock", pickNum);
        params.put("createId", AppStart.GetInstance().getUserID());
        params.put("whid", AppStart.GetInstance().Warehouse);
        String whCode = TransactSQL.instance.filterSQL(et_whcodeNOBU.getText().toString());
        params.put("inPosFullCode", whCode);
        params.put("msg", "output-varchar-8000");

        List<Hashtable> data = new ArrayList<>();
        data = Datarequest.GETstored(params);
        if (data.get(0).get("result").toString().equals("0.0")) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setMessage("补货成功！").show();
            Intent intent = new Intent();
            intent.setClass(this, PickingAreaUp.class);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(data.get(0).get("msg").toString())
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    //返回至任务界面
    public void backToPickUpMenuBU(View v) {
        Intent intent = new Intent(PickingRepair.this, PickingAreaUp.class);
        startActivity(intent);
        PickingRepair.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(PickingRepair.this, PickingAreaUp.class);
            startActivity(intent);
            PickingRepair.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    class PickingAdapters extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public PickingAdapters(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mListPos.size();
        }

        @Override
        public Object getItem(int position) {
            return mListPos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gridtextbu, null);
            }
            TextView postext = (TextView) convertView.findViewById(R.id.postext);
            String pos = "";
            for (int i = 0; i < mListPos.size(); i++) {
                if (!mListPos.get(position).get("realStock").toString().equals("")) {
                    pos = "<font color='#ff0000'>" + mListPos.get(position).get("pos").toString() + "</font>";
                } else {
                    pos = mListPos.get(position).get("pos").toString();
                }
                postext.setText(Html.fromHtml(pos));
            }
            return convertView;
        }
    }


}
