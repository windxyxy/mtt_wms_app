package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/6/22.
 */
public class out_picking extends buttom_state {

    private TextView mgoNo, wareCode, goodName, surplussum, MerchantCode;
    private EditText postion, goodCode, pickingsum;


    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器

    private int sum = 0, sums = 0, realStock = 0;
    private String nextsku = "";
    private List<Hashtable> listData = new ArrayList<Hashtable>();
    private List<Hashtable> querypick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.out_pickings);

//        Intent intent = this.getIntent();
//        querypick = (List<Hashtable>) intent.getSerializableExtra("qurypick");
//        boolean flag = true;
//        while (flag){
//            for (int i = 0; i <querypick.size() ; i++) {
//                Log.d("XiaoTest","querypick====="+querypick.get(i).get("outStockNo").toString());
//            }
//            flag = false;
//        }



        Hashtable Param = new Hashtable<>();
        Param.put("SPName", "sp_out_stock_detail_get");
        Param.put("outstockno ",AppStart.GetInstance().outorder);
        int userid = AppStart.GetInstance().getUserID();
        Param.put("currid", "" + userid + "");
        Param.put("whid", "" + AppStart.GetInstance().Warehouse + "");
        Param.put("msg", "output-varchar-500");
        querypick = Datarequest.GETstored(Param);
        if (querypick.size() != 1) {
            querypick.remove(0);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("出库完成")
                    .setCancelable(false)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            intent.setClass(out_picking.this, the_library.class);
                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                            out_picking.this.finish();
                        }
                    })
                    .show();

            return;
        }


        gridview = (GridView) findViewById(R.id.gridView);
        mgoNo = (TextView) findViewById(R.id.editText56);
        wareCode = (TextView) findViewById(R.id.textView140);
        MerchantCode = (TextView) findViewById(R.id.textView142);
        goodName = (TextView) findViewById(R.id.textView144);
        surplussum = (TextView) findViewById(R.id.textView146);

        /*名称超出滚动*/
        roll(goodName);

        mgoNo.setText(AppStart.GetInstance().outorder);
        wareCode.setText(querypick.get(0).get("wareGoodsCodes").toString());
        MerchantCode.setText(querypick.get(0).get("itemCode").toString());
        goodName.setText(querypick.get(0).get("goodsName").toString());
        surplussum.setText(querypick.get(0).get("num").toString());


        /*查询货区下的库存*/
        String SQL = "select *from v_stock where wareGoodsCodes='" + querypick.get(0).get("wareGoodsCodes").toString() + "' and (whAareType='BH' or whAareType='JH' or whAareType='SH') and warehouseId=" + AppStart.GetInstance().Warehouse + "";
        listData = Datarequest.GetDataArrayList(SQL);
        if (listData.size() != 0) {

            /*如果没有数据刷新表格重新加载*/
            srcTable = new ArrayList<HashMap<String, String>>();
            saTable = new SimpleAdapter(this,
                    srcTable,// 数据来源
                    R.layout.gridtext,//XML实现
                    new String[]{"ItemText", "ItemTexts"},  // 动态数组与ImageItem对应的子项
                    new int[]{R.id.ItemText, R.id.ItemTexts});
            // 添加并且显示
            gridview.setAdapter(saTable);
            //添加表头
            addHeader();
            //添加数据测试
            addData();
            setListViewHeightBasedOnChildren(gridview);

        } else {/*AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
            build.setMessage("库存不足！").show();*/
        }


        /*表格获取焦点*/
        gridview.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    /*获取焦点*/
                    postion.setFocusable(true);
                    postion.setFocusableInTouchMode(true);
                    postion.requestFocus();
                }
            }
        });

        /*输入验证*/
        postion = (EditText) findViewById(R.id.editText45);
        goodCode = (EditText) findViewById(R.id.editText46);
        pickingsum = (EditText) findViewById(R.id.editText29);
        /*获取焦点*/
        postion.setFocusable(true);
        postion.setFocusableInTouchMode(true);
        postion.requestFocus();


        //输入框值change事件
        pickingsum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {

                        if (!pickingsum.getText().toString().equals("")) {
                            int jhsum = 0;

                            try {
                                jhsum = Integer.parseInt(pickingsum.getText().toString());
                            } catch (Exception e) {
                            }

                            int demand = Integer.parseInt(surplussum.getText().toString());
                            if (/*jhsum > realStock ||*/ jhsum <= 0 || jhsum > demand) {
                                pickingsum.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
                                build.setMessage("拣货数量不正确！").show();
                            }

                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });


       /*货位回车事件*/
        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums % 2 != 0) {
                        if (querypos()) {
                            selectall(postion);
                            return true;
                        } else {
                            return false;
                        }

                    }

                }
                return true;
            }
        });



       /*货品条码回车事件*/
        goodCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        querycode();
                    }
                    return true;
                }
                return true;
            }
        });



      /*失去焦点触发事件*/
        postion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querypos();
                }
            }
        });

         /*失去焦点触发事件*/
        goodCode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querycode();
                }
            }
        });


    }


    public void outsava(View v) {

        String postiontext = TransactSQL.instance.filterSQL(postion.getText().toString());
        if (postiontext.equals("")) {
            postion.setError("请输入正确的货位！");
            return;
        }
        String goodCodetext = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if (!goodCodetext.equals(wareCode.getText().toString())) {
            goodCode.setError("货品不匹配！");
            return;
        }
        if (pickingsum.getText().toString().equals("")) {
            pickingsum.setError("请输入拣货数量！");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您将保存信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        sumbit();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /*完成提交*/
    public void sumbit() {
        String postiontext = TransactSQL.instance.filterSQL(postion.getText().toString());
        Hashtable ParamValues = new Hashtable<>();

        ParamValues.put("SPName", "sp_out_stock_save");
        ParamValues.put("msg", "output-varchar-500");
        ParamValues.put("detailid", querypick.get(0).get("indexId").toString());
        ParamValues.put("num", pickingsum.getText().toString());
        int userId = AppStart.GetInstance().getUserID();
        ParamValues.put("currid", "" + userId + "");
        ParamValues.put("poscode", postiontext);
        ParamValues.put("whid", AppStart.GetInstance().Warehouse);


        List<Hashtable> data = new ArrayList<Hashtable>();
        data = Datarequest.GETstored(ParamValues);
        if (data.get(0).get("result").toString().equals("0.0")) {
            AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
            build.setMessage("拣货成功！").show();
            Intent intent = new Intent();
            intent.setClass(out_picking.this, out_picking.class);
            startActivity(intent);/*调用startActivity方法发送意图给系统*/
            out_picking.this.finish();
        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
            build.setMessage(data.get(0).get("msg").toString()).show();
        }


    }


    /*查询货位*/
    public Boolean querypos() {

        goodCode.setText("");
        pickingsum.setText("");
        nextsku = "";
        String bhposition = TransactSQL.instance.filterSQL(postion.getText().toString());

        if (bhposition.equals("")) {
            return true;
        }

        for (Map l : listData) {
            if (l.get("posCode").toString().equals(bhposition)) {
                realStock = Integer.parseInt(l.get("realStock").toString());
                return false;
            }
        }

        postion.setError("货位信息不匹配！");
        return true;
    }

    /*查询货品*/
    public Boolean querycode() {

        String values = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        selectall(goodCode);
        if (values.equals("")) {
            nextsku = "";
            return true;
        }

        if (!values.equals(wareCode.getText().toString())) {
            nextsku = goodCode.getText().toString();
            goodCode.setError("货品条码不正确！");
            selectall(goodCode);
        } else {
            selectall(goodCode);
            repeat();
            nextsku = goodCode.getText().toString();
        }

        return true;
    }


    public void repeat() {

        if (nextsku.equals(goodCode.getText().toString())) {
            int sursum = 0;
            try {
                sursum = Integer.parseInt(pickingsum.getText().toString());
            } catch (Exception e) {
            }
            sursum++;

            if (realStock == 0) {
                AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
                build.setMessage("请扫入库存大于0的货位编码！").show();
                return;
            }

            int surplue = Integer.parseInt(surplussum.getText().toString());
            if (sursum <= realStock && sursum <= surplue) {
                pickingsum.setText("" + sursum + "");
            } else {
                AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
                build.setMessage("拣货数量不正确！").show();
            }

        } else {
            pickingsum.setText("1");
        }

    }


    /*表格头部*/
    public void addHeader() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText", "货位编码");
        map.put("ItemTexts", "库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格数据绑定*/
    public void addData() {
        for (Map i : listData) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText", i.get("posCode").toString());
            map.put("ItemTexts", i.get("realStock").toString());
            srcTable.add(map);
        }
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格列宽固定*/
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }


    public void out_pickingback(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(out_picking.this, the_library.class);
        startActivity(myIntent);
        out_picking.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(out_picking.this, the_library.class);
            startActivity(myIntent);
            out_picking.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
