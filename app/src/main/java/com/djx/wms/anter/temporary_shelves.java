package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
public class temporary_shelves extends buttom_state {
    private  List<Hashtable> listhas = new ArrayList<Hashtable>();
    private  List<Hashtable> bhlsit = new ArrayList<Hashtable>();

    private int sum=0;
    /* private Spinner  brand;
    private Spinner mySpinner;
    private String postionid="";*/

    private  Boolean statce=false;
    private  Boolean sumstace=false;


    private String LocationID="",goodzhu="",nextsku="",shelforder="";
    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary_shelves);
        /*货品名称滚动*/
        TextView editText26=(TextView)findViewById(R.id.editText26);
        editText26.setMovementMethod(new ScrollingMovementMethod());
        roll(editText26);

        gridview=(com.djx.wms.anter.tags)findViewById(R.id.gridView);



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





        EditText editText25=(EditText)findViewById(R.id.editText25);
        //输入框值change事件
        editText25.addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void afterTextChanged(Editable s) {
                        LocationID = "";
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });





        editText25.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    EditText editText25 = (EditText) findViewById(R.id.editText25);
                    if (Checklocation()) {
                        editText25.setFocusable(true);
                        editText25.setFocusableInTouchMode(true);
                        editText25.requestFocus();
                        return true;
                    }

                    return false;
                }
                return false;
            }
        });




         /*失去焦点触发事件*/
        editText25.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Checklocation();
                }
            }
        });













        EditText editText32=(EditText)findViewById(R.id.editText32);
        /*回车事件复写*/
        editText32.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        if (enters(v)) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    return true;
                }
                return true;
            }
        });



        /*失去焦点触发事件*/
        editText32.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                        enters(v);
                }


            }
        });

        editText32.setFocusable(true);
        editText32.setFocusableInTouchMode(true);
        editText32.requestFocus();


    }

    /* 查询货位*/
    public Boolean Checklocation(){


        EditText editText25 = (EditText) findViewById(R.id.editText25);
        String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
        if(Text25.equals("")){
            return true;
        }

        Hashtable ParamValue = new Hashtable<>();
        ParamValue.put("SPName", "PRO_GETPOSITIONID");
        String username = AppStart.GetInstance().initUserEntity();
        ParamValue.put("whid", AppStart.GetInstance().Warehouse);
        ParamValue.put("PosFullCode", Text25);
        ParamValue.put("type", "");
        ParamValue.put("msg", "output-varchar-100");
        List<Hashtable> result = new ArrayList<Hashtable>();
        result = Datarequest.GETstored(ParamValue);
        if (result.get(0).get("result").toString().equals("0.0")) {
            LocationID = result.get(0).get("msg").toString();
        } else {
            editText25.setError(result.get(0).get("msg").toString());
            editText25.setText(editText25.getText().toString());//添加这句后实现效果
            Spannable content = editText25.getText();
            Selection.selectAll(content);
            return true;
        }


        return false;
    }

    public  Boolean enters(View v){

            EditText editText27 = (EditText) findViewById(R.id.editText32);
            editText27.setText(editText27.getText().toString());//添加这句后实现效果
            Spannable content = editText27.getText();
            Selection.selectAll(content);

               if(quergood()){
                return true;
               }else {
                 if(!editText27.getText().toString().equals("")){
                 nextsku=editText27.getText().toString();
                }

                return false;
            }


    }
    /*查询货品*/
    public Boolean  quergood(){
        Boolean open=true;
        EditText editText27=(EditText)findViewById(R.id.editText32);
        String sku=editText27.getText().toString();
        TextView editText26=(TextView)findViewById(R.id.editText26);

        sku = TransactSQL.instance.filterSQL(sku);
        if(sku.equals("")){
            return  true;
        }

        String SQL="select *from   v_querygoods_android  where  wareGoodsCodes='"+sku+"' and warehouseId="+AppStart.GetInstance().Warehouse+" ";
        listhas= Datarequest.GetDataArrayList(SQL);
        if(listhas.size()!=0){
            TextView TextView26=(TextView)findViewById(R.id.editText26);
            TextView26.setText(listhas.get(0).get("goodsName").toString());
            EditText editText28=(EditText)findViewById(R.id.editText28);
            editText28.setText("1");

            /*查询货品对应的备货区下的库存列表*/
            String SQLs="  select *from v_stock where (whAareType='BH' or whAareType='JH') and  warehouseId="+AppStart.GetInstance().Warehouse+" and wareGoodsCodes='"+sku+"'";
            bhlsit= Datarequest.GetDataArrayList(SQLs);
            if(bhlsit.size()!=0){
                gridRefresh();
                //添加数据测试
                addData();
                setListViewHeightBasedOnChildren(gridview);
            }else {
                gridRefresh();
            }

            statce=true;
            return false;
        }else {
            gridRefresh();

            EditText edit27=(EditText)findViewById(R.id.editText32);
            edit27.setError("商品信息不存在，请同步商品信息！");
            TextView text=(TextView)findViewById(R.id.editText26);
            text.setText("");
            statce=false;
            return true;
        }




    }


    /*返回事件*/
    public void  purchase_back(View v){

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否返回入库菜单！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent();
                        myIntent = new Intent(temporary_shelves.this, twolevel_menu.class);
                        startActivity(myIntent);
                        temporary_shelves.this.finish();
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
    /*保存方法*/
    public  void savagood(View v){

        EditText editText25=(EditText)findViewById(R.id.editText25);
        String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
        if(Checklocation()){
            editText25.setError("请扫入正确的货位！");
            return;
        }


        EditText editText27=(EditText)findViewById(R.id.editText32);
        String Text27 = TransactSQL.instance.filterSQL(editText27.getText().toString());
        if(Text27.equals("")){
            editText27.setError("请输入正确的货品条码！");
            return;
        }


        TextView edit26=(TextView)findViewById(R.id.editText26);
        String Text26 = edit26.getText().toString();
        if(Text26.equals("")){
            AlertDialog.Builder builds = new AlertDialog.Builder(temporary_shelves.this);
            builds.setMessage("请输入正确的货品条码！").show();
            return;
        }


        EditText editsum=(EditText)findViewById(R.id.editText28);
        String Textsum = editsum.getText().toString();
        if(Textsum.equals("")||Textsum.equals("0")){
            editsum.setError("数量不能为空或0！");
            return;
        }



        EditText editText=(EditText)findViewById(R.id.editText32);
        String sku=editText.getText().toString();
        sku = TransactSQL.instance.filterSQL(sku);
        String SQL="select *from   v_querygoods_android  where  wareGoodsCodes='"+sku+"' and warehouseId="+AppStart.GetInstance().Warehouse+" ";
        listhas= Datarequest.GetDataArrayList(SQL);
        if(listhas.size()!=0){
        }else {
            editText.setError("商品信息不存在，请同步商品信息！");
            return;
        }


       /* if(LocationID.equals("")) {
            editText25.setError("请扫入货位！");
            return;
        }*/

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将上架该商品！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        EditText editText25=(EditText)findViewById(R.id.editText25);
                        String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());

                        Hashtable ParamValues = new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
                        ParamValues.put("mgotype", "1");
                        ParamValues.put("mgoNo", "");

                        EditText editText=(EditText)findViewById(R.id.editText32);
                        String sku=editText.getText().toString();
                        sku = TransactSQL.instance.filterSQL(sku);
                        ParamValues.put("wareGoodsCodes", sku);

                        EditText editText28 = (EditText) findViewById(R.id.editText28);
                        String Text28 = TransactSQL.instance.filterSQL(editText28.getText().toString());
                        ParamValues.put("realStock", Text28);
                        int userId = AppStart.GetInstance().getUserID();
                        ParamValues.put("createId", ""+userId+"");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);
                        ParamValues.put("posFullCode", "");
                        ParamValues.put("inPosFullCode", Text25);
                        ParamValues.put("msg", "output-varchar-8000");



                        ParamValues.size();
                        List<Hashtable> results = new ArrayList<Hashtable>();
                        results = Datarequest.GETstored(ParamValues);
                        if (results.get(0).get("result").toString().equals("0.0")) {

                            new AlertDialog.Builder(temporary_shelves.this)
                                    .setTitle("提示")
                                    .setMessage("是否继续上架！")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            Intent myIntent = new Intent();
                                            myIntent = new Intent(temporary_shelves.this, temporary_shelves.class);
                                            startActivity(myIntent);
                                            temporary_shelves.this.finish();
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent myIntent = new Intent();
                                            myIntent = new Intent(temporary_shelves.this, twolevel_menu.class);
                                            startActivity(myIntent);
                                            temporary_shelves.this.finish();
                                        }
                                    })
                                    .show();
                        }else {
                            AlertDialog.Builder build = new AlertDialog.Builder(temporary_shelves.this);
                            build.setMessage(results.get(0).get("msg").toString()).show();
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


    /*表格刷新*/
    public void gridRefresh(){
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
         /*失去焦点触发事件*/
        gridview.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    EditText editText25 = (EditText) findViewById(R.id.editText25);
                    editText25.setFocusable(true);
                    editText25.setFocusableInTouchMode(true);
                    editText25.requestFocus();
                    editText25.requestFocusFromTouch();
                }
            }
        });

    }





    /*返回键拦截*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否返回入库菜单！")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent();
                            myIntent = new Intent(temporary_shelves.this, twolevel_menu.class);
                            startActivity(myIntent);
                            temporary_shelves.this.finish();
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



    /*表格头部*/
    public void addHeader(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","货位");
        map.put("ItemTexts","库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }
    /*表格数据绑定*/
    public void addData(){
        for(Map i:bhlsit){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",i.get("posCode").toString());
            map.put("ItemTexts",i.get("realStock").toString());
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



}
