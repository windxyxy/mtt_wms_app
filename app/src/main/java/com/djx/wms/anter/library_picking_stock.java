package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
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
 * Created by gfgh on 2016/6/2.
 */
public class library_picking_stock extends buttom_state{


    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    public GridView gridviews;
    protected ArrayList<HashMap<String, String>> srcTables;
    protected SimpleAdapter saTables;// 适配器


    private List<Hashtable> listData = new ArrayList<Hashtable>();
    private List<Hashtable> warelist = new ArrayList<Hashtable>();

    private TextView mgoNo,wareCode,goodName,surplussum,taskorder;
    private EditText postion,goodCode,pickingsum;
    private int sums=0,sum=0,cksum=0;
    private String nextsku="";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_picking_stock);

        taskorder=(TextView)findViewById(R.id.editText56);
        postion=(EditText)findViewById(R.id.editText45);
        goodCode=(EditText)findViewById(R.id.editText46);
        pickingsum=(EditText)findViewById(R.id.editText29);
        Getfocus(postion);

        taskorder.setText(AppStart.GetInstance().Jgorder);
        String SQL="select * from t_out_stock_detail where outStockCode='"+AppStart.GetInstance().Jgorder+"' ";
        listData = Datarequest.GetDataArrayList(SQL);
        if(listData.size()==0){
            AlertDialog.Builder build = new AlertDialog.Builder(library_picking_stock.this);
            build.setMessage("该订单商品信息！").show();
            return;
        }


        /*商家编码表格*/
        gridview = (GridView) findViewById(R.id.gridView);
        /*如果没有数据刷新表格重新加载*/
        srcTable = new ArrayList<HashMap<String, String>>();
        saTable = new SimpleAdapter(this,
                srcTable,// 数据来源
                R.layout.griditem,//XML实现
                new String[] {"ItemText","ItemTexts","ItemText_there"},  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText,R.id.ItemTexts,R.id.ItemText_there});
        // 添加并且显示
        gridview.setAdapter(saTable);
        //添加表头
        addHeader();
        //添加数据测试
        addData();
        setListViewHeightBasedOnChildren(gridview);




       /*----------------------------------------------------------------------*/
        /*货位表格*/
        gridviews = (GridView) findViewById(R.id.gridViews);
                 /*如果没有数据刷新表格重新加载*/
        srcTables = new ArrayList<HashMap<String, String>>();
        saTables = new SimpleAdapter(library_picking_stock.this,
                srcTables,// 数据来源
                R.layout.gridtext,//XML实现
                new String[] {"ItemText","ItemTexts"},  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText,R.id.ItemTexts});
        // 添加并且显示
        gridviews.setAdapter(saTables);
        //添加表头
        addHeaders();







        /*商家编码表格点击查询*/
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String wareGoodsCodes = listData.get((position - 1)).get("wareGoodsCodes").toString();
                String SQL = "select *from v_stock where  wareGoodsCodes='" + wareGoodsCodes + "' and warehouseId=" + AppStart.GetInstance().Warehouse + " and (whAareType='BH' or whAareType='JH')";
                warelist = Datarequest.GetDataArrayList(SQL);
                if (warelist.size() != 0) {

                    gridviews = (GridView) findViewById(R.id.gridViews);
                   /*如果没有数据刷新表格重新加载*/
                    srcTables = new ArrayList<HashMap<String, String>>();
                    saTables = new SimpleAdapter(library_picking_stock.this,
                            srcTables,// 数据来源
                            R.layout.gridtext,//XML实现
                            new String[]{"ItemText", "ItemTexts"},  // 动态数组与ImageItem对应的子项
                            new int[]{R.id.ItemText, R.id.ItemTexts});
                    // 添加并且显示
                    gridviews.setAdapter(saTables);
                    //添加表头
                    addHeaders();
                    //添加数据测试
                    addDatas();
                    setListViewHeightBasedOnChildren(gridviews);
                } else {
                    AlertDialog.Builder build = new AlertDialog.Builder(library_picking_stock.this);
                    build.setMessage("该货品没有库存！").show();
                }


            }

        });



        /*回车事件复写*/
        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        postion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querypos();
                }
            }
        });






       /*回车事件复写*/
        goodCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        querycode();
                    }
                }
                return true;
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








        pickingsum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!pickingsum.getText().toString().equals("")) {
                            int jsum = 0;
                            try {jsum = Integer.parseInt(pickingsum.getText().toString());} catch (Exception e) {}
                            if (jsum > cksum || jsum <= 0) {
                                pickingsum.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(library_picking_stock.this);
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


    }




    public Boolean querypos(){
        goodCode.setText("");
        pickingsum.setText("");
        nextsku="";


        String  bhposition = TransactSQL.instance.filterSQL(postion.getText().toString());
        for (Map l : warelist) {
            if (l.get("posCode").toString().equals(bhposition)) {
                return false;
            }
        }


        postion.setError("货位信息不匹配！");
        selectall(postion);
        return true;
    }





    public Boolean querycode(){

        selectall(goodCode);
        String values = goodCode.getText().toString();
        values = TransactSQL.instance.filterSQL(values);
        String pos= TransactSQL.instance.filterSQL(postion.getText().toString());

        /* 货品代码重复判断*/
        if(nextsku.equals(values)){
            int versum=0;
            try{versum=Integer.parseInt(pickingsum.getText().toString());}catch (Exception e){}
            versum++;
            if(versum<=cksum){pickingsum.setText(""+versum+"");}
            return true;
        }
        nextsku=values;



        Boolean goodstaus=true;
        Boolean posstaus=true;
        for(Map k:listData){
            if(k.get("wareGoodsCodes").toString().equals(values)){

                goodstaus=false;
                for(Map cos:warelist){
                    if(cos.get("wareGoodsCodes").toString().equals(values) && cos.get("posCode").toString().equals(pos)){
                        cksum=Integer.parseInt(cos.get("realStock").toString());
                        posstaus=false;
                        pickingsum.setText("1");
                    }
                }



            }
        }


      if(goodstaus){
          goodCode.setError("请输入正确的货品条码！");
      }else if(posstaus) {
          postion.setError("请输入正确的货位编码！");
      }
        return true;
    }


    public void savalibrary(View v){

     /*
        if(querypos()){
            postion.setText(postion.getError().toString());
            return;
        }*/

       if(pickingsum.getText().toString().equals("") || pickingsum.getText().toString().equals("0")){
           pickingsum.setError("请输入正确的拣货数量！");
           return;
       }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        String values = TransactSQL.instance.filterSQL(goodCode.getText().toString());
                        String pos= TransactSQL.instance.filterSQL(postion.getText().toString());
                        Hashtable ParamValues =new Hashtable<>();
                        ParamValues.put("SPName", "PRO_STOREPROCESS_HANDHELD_ADD");
                        ParamValues.put("msg", "output-varchar-8000");
                        ParamValues.put("outStockNo", AppStart.GetInstance().Jgorder);
                        ParamValues.put("wareGoodsCodes",values);
                        ParamValues.put("realStock",pickingsum.getText().toString());
                        int  userId=AppStart.GetInstance().getUserID();
                        ParamValues.put("createId",""+userId+"");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);
                        ParamValues.put("posFullCode",pos);


                        List<Hashtable> data = new ArrayList<Hashtable>();
                        data = Datarequest.GETstored(ParamValues);
                        if(data.get(0).get("result").toString().equals("0.0")){



                            new AlertDialog.Builder(library_picking_stock.this)
                                    .setTitle("提示")
                                    .setMessage("是否继续拣货？")


                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            Intent intent = new Intent();
                                            intent.setClass(library_picking_stock.this, library_picking_stock.class);
                                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                                            library_picking_stock.this.finish();
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setClass(library_picking_stock.this, library_processing.class);
                                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                                            library_picking_stock.this.finish();
                                        }
                                    })
                                    .show();

                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(library_picking_stock.this);
                            build.setMessage(data.get(0).get("msg").toString()).show();
                        }


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















    /*表格头部*/
    public void addHeader(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText", "商家编码");
        map.put("ItemTexts","实际数量");
        map.put("ItemText_there","已拣数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }
    /*表格数据绑定*/
    public void addData(){
        for(Map i:listData){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",i.get("itemCode").toString());
            map.put("ItemTexts",i.get("planQty").toString());
            map.put("ItemText_there",i.get("realQty").toString());
            srcTable.add(map);
        }
        saTable.notifyDataSetChanged();//更新数据
    }



    /*表格头部*/
    public void addHeaders(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","货位编码");
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






    /*返回*/
    public void backlibrary_processing(View v){

        new AlertDialog.Builder(library_picking_stock.this)
                .setTitle("提示")
                .setMessage("是否退出！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent();
                        myIntent = new Intent(library_picking_stock.this, library_processing.class);
                        startActivity(myIntent);
                        library_picking_stock.this.finish();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键


            new AlertDialog.Builder(library_picking_stock.this)
                    .setTitle("提示")
                    .setMessage("是否退出！")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent();
                            myIntent = new Intent(library_picking_stock.this, library_processing.class);
                            startActivity(myIntent);
                            library_picking_stock.this.finish();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





}
