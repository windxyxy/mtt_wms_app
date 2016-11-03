package com.djx.wms.anter;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gfgh on 2016/3/15.
 */
public class single_shelves extends buttom_state {

    private GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_shelves);


            gridview = (GridView) findViewById(R.id.gridViews);
            srcTable = new ArrayList<HashMap<String, String>>();
            saTable = new SimpleAdapter(this,
                    srcTable,// 数据来源
                    R.layout.griditem,//XML实现
                    new String[] { "ItemText" },  // 动态数组与ImageItem对应的子项
                    new int[] { R.id.ItemText });
            // 添加并且显示
            gridview.setAdapter(saTable);



            // 添加消息处理
            /*gridview.setOnItemClickListener(new ItemClickListener());*/

            //添加表头
            addHeader();

            //添加数据测试
            addData();
            setListViewHeightBasedOnChildren(gridview);

        }






    public void singleback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(single_shelves.this,twolevel_menu.class);
        startActivity(myIntent);
        single_shelves.this.finish();
    }

    public void creatshelf(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(single_shelves.this,manualshelves.class);
        startActivity(myIntent);
        /*single_shelves.this.finish();*/
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
//land
        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
//port
        }
    }




    public void addHeader(){
        String items[]={ "入库单", "入库时间","货量"};
        for (String strText:items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",strText);
            srcTable.add(map);
        }
        saTable.notifyDataSetChanged(); //更新数据
    }

    public void addData(){
        for(int i=0;i<10;i++){
            String items[]={"LJ45545565", "2016-3-15","12"};
            for (String strText:items) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemText",strText);
                srcTable.add(map);
            }
        }
        saTable.notifyDataSetChanged(); //更新数据
    }

    //清空列表
    public void RemoveAll()
    {
        srcTable.clear();
        saTable.notifyDataSetChanged();
    }



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



