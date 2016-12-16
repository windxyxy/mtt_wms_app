
package com.djx.wms.anter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/4/7.
 */

public class reple_goods extends buttom_state {

    private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> gridData = new ArrayList<Map<String, String>>();
    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器

    private int sum = 0, sumpos = 0, bhsum = 0, sums = 0;
    private String postionID = "";
    private Boolean pos = true;
    private Boolean statce = false;
    private Boolean sumstatce = false;
    private AlertDialog prompt = null;
    private String Stocking = "", replenorder = "", nextsku = "", goodCode = "",planStock="";

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reple_goods);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        replenorder = intent.getStringExtra("order");
        planStock = intent.getStringExtra("planStock");//---



        gridData = (List) bundle.getSerializable("griddata");
        TextView editText18 = (TextView) findViewById(R.id.editText56);
        TextView editText19 = (TextView) findViewById(R.id.editText57);
        TextView editText20 = (TextView) findViewById(R.id.editText58);
        TextView tv_yubu = (TextView) findViewById(R.id.tv_yubu);

        /*货品名称滚动*/
        roll(editText20);


        goodCode = gridData.get(0).get("wareGoodsCodes").toString();
        editText18.setText(gridData.get(0).get("goodsCode").toString());
        editText19.setText(gridData.get(0).get("wareGoodsCodes").toString());
        editText20.setText(gridData.get(0).get("goodsName").toString());
        tv_yubu.setText(planStock);//---


        gridview = (GridView) findViewById(R.id.gridView);
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

        EditText editText29 = (EditText) findViewById(R.id.editText29);
        //输入框值change事件
        editText29.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText editText29 = (EditText) findViewById(R.id.editText29);
                        if (!editText29.getText().toString().equals("")) {
                            int jsum = 0;
                            try {
                                jsum = Integer.parseInt(editText29.getText().toString());
                            } catch (Exception e) {
                            }

                            if (/*jsum > bhsum || */jsum <= 0) {
                                editText29.setText("");
                                editText29.setError("拣货数量不正确！");
                                pos = false;
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


        EditText editText21 = (EditText) findViewById(R.id.editText45);
        editText21.setFocusable(true);
        editText21.setFocusableInTouchMode(true);
        editText21.requestFocus();

        /*回车事件复写*/
        editText21.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums % 2 != 0) {
                        if (querypos()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        });


        /*失去焦点触发事件*/
        editText21.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querypos();
                }
            }
        });

        EditText editText22 = (EditText) findViewById(R.id.editText46);
       /*失去焦点触发事件*/
        editText22.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querycode();
                }
            }
        });







        /*回车事件复写*/
        editText22.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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




    /*    int toal=0;
        for(Map cos:gridData){
           int tatol= Integer.parseInt(cos.get("realStock").toString());
           toal=toal+tatol;
        }
      *//* 货位问题为null，提示*//*
        if(toal<=0){
              prompt = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(""+Stocking+"备货区下没有库存！")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent intents = new Intent();
                            intents.setClass(reple_goods.this, reple_task.class);
                            startActivity(intents);*//*调用startActivity方法发送意图给系统*//*
                            reple_goods.this.finish();
                            prompt.dismiss();

                        }
                    })
                    .show();
        }*/

    }

    public Boolean repeat() {

        EditText edit22 = (EditText) findViewById(R.id.editText46);
        String values = edit22.getText().toString();
        values = TransactSQL.instance.filterSQL(values);


        EditText editText29 = (EditText) findViewById(R.id.editText29);
        if (nextsku.equals(values) && !nextsku.equals("")) {
            int sum = 0;
            try {
                sum = Integer.parseInt(editText29.getText().toString());
            } catch (Exception e) {
            }

            sum = sum + 1;
            if (sum > bhsum || sum <= 0) {
                AlertDialog.Builder build = new AlertDialog.Builder(reple_goods.this);
                build.setMessage("拣货数量不正确！").show();
                statce = false;
                return false;
            }
            editText29.setText("" + sum + "");
        } else {

            EditText edit21 = (EditText) findViewById(R.id.editText45);
            String bhposition = edit21.getText().toString();
            bhposition = TransactSQL.instance.filterSQL(bhposition);
            Map<String, String> positionquer = new HashMap<String, String>();
            for (Map g : gridData) {
                if (g.get("wareGoodsCodes").toString().equals(values)) {
                    positionquer = g;
                    if (g.get("posCode").toString().equals(bhposition)) {
                        bhsum = Integer.parseInt(positionquer.get("realStock").toString());
                    }
                }
            }

            /*  */
            if (positionquer.size() != 0) {
                statce = true;
                editText29.setText("1");
            } else {
                AlertDialog.Builder build = new AlertDialog.Builder(reple_goods.this);
                build.setMessage("该货位下不存在此信息，请检查货位和货品条码！").show();
                return false;
            }

        }
        return true;
    }


    public void savareple(View v) {

        EditText text21 = (EditText) findViewById(R.id.editText45);
        if (text21.getText().toString().equals("")) {
            text21.setError("请输入正确的货位！");
            return;
        }

        EditText edit22 = (EditText) findViewById(R.id.editText46);
        String values = edit22.getText().toString();
        values = TransactSQL.instance.filterSQL(values);

        if (!values.equals(goodCode)) {
            edit22.setError("货品不匹配！");
            return;
        }

        EditText edit29 = (EditText) findViewById(R.id.editText29);
        if (edit29.getText().toString().equals("")) {
            edit29.setError("请输入拣货数量！");
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")

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

        Hashtable ParamValues = new Hashtable<>();
        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
        ParamValues.put("msg", "output-varchar-8000");
        ParamValues.put("mgotype", "2");
        ParamValues.put("mgoNo", replenorder);

        EditText text22 = (EditText) findViewById(R.id.editText46);
        String wareGoodsCodes = TransactSQL.instance.filterSQL(text22.getText().toString());
        ParamValues.put("wareGoodsCodes", wareGoodsCodes);

        EditText text29 = (EditText) findViewById(R.id.editText29);
        String realStock = TransactSQL.instance.filterSQL(text29.getText().toString());
        ParamValues.put("realStock", realStock);

        int userId = AppStart.GetInstance().getUserID();
        ParamValues.put("createId", "" + userId + "");
        ParamValues.put("whid", AppStart.GetInstance().Warehouse);

        EditText text21 = (EditText) findViewById(R.id.editText45);
        String posFullCode = TransactSQL.instance.filterSQL(text21.getText().toString());
        ParamValues.put("posFullCode", posFullCode);
        ParamValues.put("inPosFullCode", "");


        List<Hashtable> data = new ArrayList<Hashtable>();
        data = Datarequest.GETstored(ParamValues);


        if (data.get(0).get("result").toString().equals("0.0")) {
//            AlertDialog.Builder build = new AlertDialog.Builder(reple_goods.this);
//            build.setMessage("拣货成功！").show();
            Toast.makeText(reple_goods.this,"拣货成功",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setClass(reple_goods.this, reple_task.class);
            startActivity(intent);/*调用startActivity方法发送意图给系统*/
//            reple_goods.this.finish();
        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(reple_goods.this);
            build.setMessage(data.get(0).get("msg").toString()).show();
        }


    }

    /*表格头部*/
    public void addHeader() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText", "备货区货位");
        map.put("ItemTexts", "库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格数据绑定*/
    public void addData() {
        for (Map i : gridData) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText", i.get("posCode").toString());
            map.put("ItemTexts", i.get("realStock").toString());

            if (i.get("realStock").toString().equals("")) {
                sumstatce = true;
                Stocking = i.get("posCode").toString();
            }

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


    public void backtask(View v) {
        Intent intent = new Intent();
        intent.setClass(reple_goods.this, reple_task.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        reple_goods.this.finish();
    }

    public void backmain(View v) {
        Intent intent = new Intent();
        intent.setClass(reple_goods.this, library_management.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        reple_goods.this.finish();
    }

    public Boolean querypos() {

        EditText editText46 = (EditText) findViewById(R.id.editText46);
        EditText editText29 = (EditText) findViewById(R.id.editText29);
        editText46.setText("");
        editText29.setText("");
        nextsku = "";
        EditText editText45 = (EditText) findViewById(R.id.editText45);

        if (editText45.getText().toString().equals("")) {
            return false;
        }

        String bhposition = editText45.getText().toString();
        bhposition = TransactSQL.instance.filterSQL(bhposition);
        for (Map l : gridData) {
            if (l.get("posCode").toString().equals(bhposition)) {
                return false;
            }
        }

        editText45.setError("货位信息不匹配！");
        editText45.setText(editText45.getText().toString());//添加这句后实现效果
        Spannable content = editText45.getText();
        Selection.selectAll(content);
        return true;
    }

    public Boolean querycode() {

             /*货品条码*/
        EditText edit22 = (EditText) findViewById(R.id.editText46);
        EditText edit29 = (EditText) findViewById(R.id.editText29);
             /*货位条码*/
        EditText postion = (EditText) findViewById(R.id.editText45);
        String values = edit22.getText().toString();
        values = TransactSQL.instance.filterSQL(values);

        String postiontext = TransactSQL.instance.filterSQL(postion.getText().toString());
        if (values.equals("")) {
            return true;
        }

        if (postiontext.equals("")) {
            return true;
        }


        if (!values.equals(goodCode)) {
            nextsku = edit22.getText().toString();
            edit22.setError("货品条码不正确！");
            edit22.setText(edit22.getText().toString());//添加这句后实现效果
            Spannable content = edit22.getText();
            Selection.selectAll(content);
        } else {
            edit22.setText(edit22.getText().toString());//添加这句后实现效果
            Spannable content = edit22.getText();
            Selection.selectAll(content);
            repeat();
            nextsku = edit22.getText().toString();
        }


        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(reple_goods.this, reple_task.class);
            startActivity(myIntent);
            reple_goods.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}


