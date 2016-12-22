package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class PickingUp extends buttom_state {
    private TextView tv_listNO, tv_whNO, tv_goodNAME, tv_busNO, tv_linecodeNO;
    private EditText et_whgoodNO, et_whcodeNO, et_pickNum;
    private GridView pickup_gridView;

    private List<HashMap<String, String>> srcListData;
    private List<Hashtable> lists;
    private SimpleAdapter mAdapter;
    private String pickOrder;
    private String wareGoodscode;


    /*
    * 初始化控件
    * */
    private void init() {
        tv_listNO = (TextView) findViewById(R.id.tv_listNO);//任务单号
        tv_whNO = (TextView) findViewById(R.id.tv_whNO);//仓库货品条码tv
        tv_goodNAME = (TextView) findViewById(R.id.tv_goodNAME);//货品名称
        tv_busNO = (TextView) findViewById(R.id.tv_busNO);//商家编码
        tv_linecodeNO = (TextView) findViewById(R.id.tv_linecodeNO);//条形码
        pickup_gridView = (GridView) findViewById(R.id.pickup_gridView);

        et_whgoodNO = (EditText) findViewById(R.id.et_whgoodNO);//仓库货品条码
        et_whcodeNO = (EditText) findViewById(R.id.et_whcodeNO);//货位编码
        et_pickNum = (EditText) findViewById(R.id.et_pickNum);//拣货数量

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_up);
        init();

        Intent intent = getIntent();
        pickOrder = intent.getStringExtra("pickOrder");
        tv_listNO.setText(pickOrder);
        wareGoodscode = intent.getStringExtra("wareGoodscode");

        String SQL = "select * from v_stock where whAareType = 'BH' and warehouseId = "+AppStart.GetInstance().Warehouse+" and  wareGoodsCodes = '"+wareGoodscode+"'";
//        String SQL = "select * from v_stock where (whAareType = 'BH' or whAareType = 'SH') and warehouseId = 仓库ID and  wareGoodsCodes = 仓库货品条码";
        lists = new ArrayList<>();
        lists = Datarequest.GetDataArrayList(SQL);
        if (lists.size()!=0){
            tv_whNO.setText(lists.get(0).get("wareGoodsCodes").toString());//仓库货品条码
            tv_goodNAME.setText(lists.get(0).get("goodsName").toString());//货品名称
            tv_busNO.setText(lists.get(0).get("goodsCode").toString());//商家编码
            tv_linecodeNO.setText(lists.get(0).get("barCodes").toString());//条形码

            srcListData = new ArrayList<>();
            mAdapter = new SimpleAdapter(this,
                    srcListData,
                    R.layout.gridtext,
                    new String[]{"textPos", "textNum"},
                    new int[]{R.id.ItemText, R.id.ItemTexts});
            pickup_gridView.setAdapter(mAdapter);
            //添加表头
            addHeader();
            //添加数据
            addData();
            setListViewHeightBasedOnChildren(pickup_gridView);

        }

        et_whgoodNO.setFocusable(true);
        et_whgoodNO.setFocusableInTouchMode(true);
        et_whgoodNO.requestFocus();

        //货品条码查询回车事件
        et_whgoodNO.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    query_whGoodNO();
                }
                return false;
            }
        });
        //货品条码查询失去焦点事件
        et_whgoodNO.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    query_whGoodNO();
                }
            }
        });
        //货位查询回车事件
        et_whcodeNO.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (qurey_whPos()){
                        return false;
                    }else {
                        et_whcodeNO.setError("货位编码不正确");
                        selectall(et_whcodeNO);
                        return true;
                    }
                }
                return false;
            }
        });
        //货位查询失去焦点事件
        et_whcodeNO.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (qurey_whPos()){

                    }else {
                        et_whcodeNO.setError("货位编码不正确");
                        selectall(et_whcodeNO);
                        return;
                    }
                }
            }
        });


    }


    //查询仓库货品条码
    private void query_whGoodNO() {
        String whGoodNO = TransactSQL.instance.filterSQL(et_whgoodNO.getText().toString());
        if (whGoodNO.equals("")) {
            et_whgoodNO.setError("货品条码不能为空");
            return;
        } else if (!whGoodNO.equals(tv_whNO.getText().toString())) {
            et_whgoodNO.setError("货品条码错误");
            selectall(et_whgoodNO);
            return;
        }
    }

    //货位查询
    private boolean qurey_whPos() {
        String pos = TransactSQL.instance.filterSQL(et_whcodeNO.getText().toString());
        if (pos.toString().equals("")){
            return false;
        }
        for (Map m : lists) {
            if (m.get("posCode").toString().equals(pos)){
                return true;
            }
        }
        return false;
    }

    //拣货区上架确定按钮
    public void makeSure(View v) {
        final String pickNum = TransactSQL.instance.filterSQL(et_pickNum.getText().toString());
        if (pickNum.equals("")){
            new AlertDialog.Builder(this)
                    .setMessage("数量不能为空")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return;
        }
        if (pickNum.equals("0")){
            new AlertDialog.Builder(this)
                    .setMessage("数量不能为0")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return;
        }
        Hashtable params = new Hashtable<>();
        params.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
        params.put("mgotype", 7);
        params.put("mgoNo", pickOrder);
        String whGoodsNo = TransactSQL.instance.filterSQL(et_whgoodNO.getText().toString());
        params.put("wareGoodsCodes",whGoodsNo);
        params.put("realStock", pickNum);
        params.put("createId", AppStart.GetInstance().getUserID());
        params.put("whid",AppStart.GetInstance().Warehouse);
        String whCode = TransactSQL.instance.filterSQL(et_whcodeNO.getText().toString());
        params.put("posFullCode",whCode);
        params.put("msg", "output-varchar-8000");

        List<Hashtable> data = new ArrayList<>();
        data = Datarequest.GETstored(params);

        if (data.get(0).get("result").toString().equals("0.0")) {
            new AlertDialog.Builder(this)
                    .setMessage("是否继续拣货")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PickingUp.this,PickingUp.class);
                            intent.putExtra("pickOrder",pickOrder);
                            String wgcode = tv_whNO.getText().toString();
                            intent.putExtra("wareGoodscode",wgcode);
                            startActivity(intent);
                            dialog.dismiss();
                            PickingUp.this.finish();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PickingUp.this,PickingAreaUp.class);
                            startActivity(intent);
                            dialog.dismiss();
                            PickingUp.this.finish();
                        }
                    })
                    .show();


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

    public void addHeader() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("textPos", "货位编码");
        hashMap.put("textNum", "库存");
        srcListData.add(hashMap);
        mAdapter.notifyDataSetChanged();
    }

    public void addData() {
        for (Map i : lists) {
            HashMap<String, String> map = new HashMap<>();
            map.put("textPos", i.get("posCode").toString());//------------------------------补充----------------------------------货位
            map.put("textNum", i.get("realStock").toString());//------------------------------补充----------------------------------库存
            srcListData.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }


    //返回到任务界面
    public void backToPickUpMenu(View view) {
        Intent intent = new Intent(PickingUp.this, PickingAreaUp.class);
        startActivity(intent);
        PickingUp.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(PickingUp.this, PickingAreaUp.class);
            startActivity(intent);
            PickingUp.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
