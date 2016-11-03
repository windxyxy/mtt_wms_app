package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
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
 * Created by gfgh on 2016/6/6.
 */
public class query_good  extends buttom_state{

    private int sum=0;
    private EditText goodCode;
    private TextView goodSku,goodname;
    private List<Hashtable> listData = new ArrayList<Hashtable>();

    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_good);

        gridview = (GridView) findViewById(R.id.gridView);
        goodCode=(EditText)findViewById(R.id.editText32);
        goodSku=(TextView)findViewById(R.id.textView137);
        goodname=(TextView)findViewById(R.id.textView138);



        /*货品名称滚动*/
        roll(goodname);

        goodCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        querycode(v);
                    }

                    return true;
                }
                return true;
            }
        });




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










    }




    /*查询货品*/
    public Boolean querycode(View v){
        selectall(goodCode);

        String Code= TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if(Code.equals("")){
            AlertDialog.Builder build = new AlertDialog.Builder(query_good.this);
            build.setMessage("商品未上架！").show();
            goodSku.setText("");
            goodname.setText("");
            return true;
        }
        String SQL="select *from v_stock where wareGoodsCodes='"+Code+"' and (whAareType='BH' or whAareType='JH') and warehouseId="+AppStart.GetInstance().Warehouse+"";
        listData = Datarequest.GetDataArrayList(SQL);
        if(listData.size()!=0){

            goodSku.setText(listData.get(0).get("goodsCode").toString());
            goodname.setText(listData.get(0).get("goodsName").toString());

             /*如果没有数据刷新表格重新加载*/
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

        }else {

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

            AlertDialog.Builder build = new AlertDialog.Builder(query_good.this);
            build.setMessage("商品未上架！").show();
            goodSku.setText("");
            goodname.setText("");

        }
        return true;
    }

    /*
    货品查询，商家编码
     */
    public Boolean  querygoodscode(View v){

        String Code= TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if(Code.equals("")){
            AlertDialog.Builder build = new AlertDialog.Builder(query_good.this);
            build.setMessage("商品未上架！").show();
            goodSku.setText("");
            goodname.setText("");
            return true;
        }
        String SQL="select *from v_stock where goodsCode='"+Code+"' and (whAareType='BH' or whAareType='JH') and warehouseId="+AppStart.GetInstance().Warehouse+"";
        listData = Datarequest.GetDataArrayList(SQL);
        if(listData.size()!=0){

            goodSku.setText(listData.get(0).get("goodsCode").toString());
            goodname.setText(listData.get(0).get("goodsName").toString());

             /*如果没有数据刷新表格重新加载*/
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

        }else {

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

            AlertDialog.Builder build = new AlertDialog.Builder(query_good.this);
            build.setMessage("商品未上架！").show();
            goodSku.setText("");
            goodname.setText("");

        }
        return true;
    }




    /*表格头部*/
    public void addHeader(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","货位编码");
        map.put("ItemTexts","库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }
    /*表格数据绑定*/
    public void addData(){
        for(Map i:listData){
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

    /*返回键拦截*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键


            Intent myIntent = new Intent();
            myIntent = new Intent(query_good.this, library_management.class);
            startActivity(myIntent);
            query_good.this.finish();



            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goodbacks(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(query_good.this, library_management.class);
        startActivity(myIntent);
        query_good.this.finish();
    }
}



