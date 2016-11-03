package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
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

/**
 * Created by gfgh on 2016/5/24.
 */
public class counter_picking extends buttom_state{



    /*private List<Map<String, String>> gridData= new ArrayList<Map<String, String>>();*/
    private List<Hashtable> gridData = new ArrayList<Hashtable>();
    private List<Hashtable>  warelist = new ArrayList<Hashtable>();
    private TextView mgoNo,wareCode,goodName,surplussum;
    private EditText postion,goodCode,pickingsum;

    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    public GridView gridviews;
    protected ArrayList<HashMap<String, String>> srcTables;
    protected SimpleAdapter saTables;// 适配器


    private int sums=0,sum=0,realStock=0,bhsum=0;
    private String replenorder="",nextsku="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_picking);



        String SQLS="select * from v_fillgoods where mgoNo = '"+AppStart.GetInstance().counterorder+"' and whId="+AppStart.GetInstance().Warehouse+" and stock!=0";
        gridData = Datarequest.GetDataArrayList(SQLS);
        if (gridData.size()!= 0){
        }else {
            AlertDialog.Builder build = new AlertDialog.Builder(counter_picking.this);
            build.setMessage("该订单已上货！").show();

            Intent intent = new Intent();
            intent.setClass(counter_picking.this, reverse_order.class);
            startActivity(intent);
            counter_picking.this.finish();
        }




        /* 获取组件*/
        mgoNo=(TextView)findViewById(R.id.editText56);
        postion=(EditText)findViewById(R.id.editText46);
        goodCode=(EditText)findViewById(R.id.editText45);
        pickingsum=(EditText)findViewById(R.id.editText29);


        mgoNo.setText(AppStart.GetInstance().counterorder);
        goodCode.setFocusable(true);
        goodCode.setFocusableInTouchMode(true);
        goodCode.requestFocus();

        /*货品查询表格初始化*/
        gridviews = (GridView) findViewById(R.id.gridViews);
        gridRefreshs();


        /*任务单号下货品表格初始化*/
        gridview = (GridView) findViewById(R.id.gridView);
        srcTable = new ArrayList<HashMap<String, String>>();
        saTable = new SimpleAdapter(this,
                srcTable,// 数据来源
                R.layout.gridtext,//XML实现
                new String[] {"ItemText","ItemTexts"},  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText,R.id.ItemTexts});
        // 添加并且显示
        gridview.setAdapter(saTable);
        //添加表头
        addHeader();
        //添加数据测试
        addData();
        setListViewHeightBasedOnChildren(gridview);




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

                            if (jhsum > realStock || jhsum <= 0 || jhsum > bhsum) {
                                pickingsum.setText("");
                                pickingsum.setError("拣货数量不正确！");
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
                            return true;
                        } else {
                            pickingsum.setText("1");
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
                        if (querycode()) {
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
        postion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!querypos()){
                        pickingsum.setText("1");
                    }

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

    /*查询货位*/
    public Boolean querypos(){

        String bhposition = TransactSQL.instance.filterSQL(postion.getText().toString());
        if(bhposition.equals("")){
            return true;
        }

        for (Map l : warelist) {
            if (l.get("posCode").toString().equals(bhposition)) {
                realStock=Integer.parseInt(l.get("realStock").toString());
                return false;
            }
        }

        postion.setError("货位信息不匹配！");
        selectall(postion);
        return true;
    }

    /*查询货品*/
    public Boolean querycode(){



        String  Code=TransactSQL.instance.filterSQL(goodCode.getText().toString());
        Boolean  Inspection_goods=false;
        for(Map i:gridData){
           if (Code.equals(i.get("wareGoodsCodes"))){
               Inspection_goods=true;
           }
        }

        if(!Inspection_goods){
            goodCode.setError("请输入正确的货品条码！");
            /* 初始化表格数据，加载表格初始化*/
            warelist=new ArrayList<Hashtable>();
            selectall(goodCode);
            gridRefresh();
            return true;
        }




        String SQL = "select * from v_stock where  whAareType='JH' and warehouseId='"+AppStart.GetInstance().Warehouse+"' and wareGoodsCodes='"+Code+"'  and realStock!=0 ";
        warelist = Datarequest.GetDataArrayList(SQL);
        if(warelist.size()==0){
            goodCode.setError("请输入正确的货品条码！");
            selectall(goodCode);

            /* 初始化表格数据，加载表格初始化*/
            warelist=new ArrayList<Hashtable>();
            gridRefresh();

            return true;
          }else {
            for(Map k:gridData){
                if(k.get("wareGoodsCodes").toString().equals(goodCode.getText().toString())){
                    bhsum=Integer.parseInt(k.get("stock").toString());
                }
            }
        }


        gridRefresh();
        return false;
    }





    /*表格刷新*/
    public void gridRefresh(){
    srcTables = new ArrayList<HashMap<String, String>>();
    saTables = new SimpleAdapter(this,
            srcTables,// 数据来源
            R.layout.gridtext,//XML实现
            new String[] {"ItemText","ItemTexts"},  // 动态数组与ImageItem对应的子项
            new int[] { R.id.ItemText,R.id.ItemTexts});
    // 添加并且显示
    gridviews.setAdapter(saTables);
    //添加表头
    addHeaders();
    //添加数据测试
    addDatas();
         setListViewHeightBasedOnChildren(gridviews);

    /*失去焦点触发事件*/
    gridviews.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                postion.setFocusable(true);
                postion.setFocusableInTouchMode(true);
                postion.requestFocus();
                postion.requestFocusFromTouch();
            }
        }
    });
}



    public void countersava(View v){

        Nonull(postion,"请输入正确的货位！");

        if(querypos()){
            postion.setError("请输入正确的货位！");
            return;
        }

        String goodCodetext = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if(goodCodetext.equals("")){
            goodCode.setError("请输入正确的货品条码！");
            return;
        }


        if(pickingsum.getText().toString().equals("")) {
            pickingsum.setError("请输入拣货数量！");
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
    public void sumbit(){
        Hashtable ParamValues =new Hashtable<>();
        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_FILL");
        ParamValues.put("msg", "output-varchar-8000");
        ParamValues.put("mgotype", "6");
        ParamValues.put("mgoNo", AppStart.GetInstance().counterorder);


        String wareGoodsCodes = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        ParamValues.put("wareGoodsCodes",wareGoodsCodes);


        String   realStock=TransactSQL.instance.filterSQL(pickingsum.getText().toString());
        ParamValues.put("realStock",realStock);

        int  userId=AppStart.GetInstance().getUserID();
        ParamValues.put("createId",""+userId+"");
        ParamValues.put("whid", AppStart.GetInstance().Warehouse);


        String   posFullCode=TransactSQL.instance.filterSQL(postion.getText().toString());
        ParamValues.put("inPosFullCode",posFullCode);



        List<Hashtable> data = new ArrayList<Hashtable>();
        data = Datarequest.GETstored(ParamValues);
        if(data.get(0).get("result").toString().equals("0.0")){
            AlertDialog.Builder build = new AlertDialog.Builder(counter_picking.this);
            build.setMessage("上货成功！").show();

            Intent Intent = new Intent();
            Intent = new Intent(counter_picking.this, counter_picking.class);
            startActivity(Intent);
            counter_picking.this.finish();
        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(counter_picking.this);
            build.setMessage(data.get(0).get("msg").toString()).show();
        }


    }




    /*表格刷新*/
    public void gridRefreshs(){
        srcTables = new ArrayList<HashMap<String, String>>();
        saTables = new SimpleAdapter(this,
                srcTables,// 数据来源
                R.layout.gridtext,//XML实现
                new String[] {"ItemText","ItemTexts"},  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText,R.id.ItemTexts});
        // 添加并且显示
        gridviews.setAdapter(saTables);
        //添加表头
        addHeaders();
    }



    /*返回*/
    public void backreverse_order(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(counter_picking.this, reverse_order.class);
        startActivity(myIntent);
        counter_picking.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(counter_picking.this, reverse_order.class);
            startActivity(myIntent);
            counter_picking.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /*表格头部*/
    public void addHeader(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","商家编码");
        map.put("ItemTexts","待上货数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格数据绑定*/
    public void addData(){
        for(Map i:gridData){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",i.get("goodsCode").toString());
            map.put("ItemTexts",i.get("stock").toString());
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
    /*表格头部*/
    public void addHeaders(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","货位");
        map.put("ItemTexts","库存数量");
        srcTables.add(map);
        saTables.notifyDataSetChanged(); //更新数据
    }
    /*表格数据绑定*/
    public void addDatas(){
        for(Map i:warelist){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",i.get("posCode").toString());
            map.put("ItemTexts",i.get("realStock").toString());
            srcTables.add(map);
        }
        saTables.notifyDataSetChanged(); //更新数据
    }



}
