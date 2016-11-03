package com.djx.wms.anter;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gfgh on 2016/3/15.
 */
public class Table extends AppCompatActivity {


    private GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature( Window.FEATURE_NO_TITLE ); //无标题
       /* setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏*/
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gridview);
        gridview = (GridView) findViewById(R.id.gridview);

        srcTable = new ArrayList<HashMap<String, String>>();
        saTable = new SimpleAdapter(this,
                srcTable,// 数据来源
                R.layout.griditem,//XML实现
                new String[] { "ItemText" },  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText });


        // 添加并且显示
        gridview.setAdapter(saTable);
        // 添加消息处理
      /*  gridview.setOnItemClickListener(new ItemClickListener());*/

        //添加表头
        addHeader();

        //添加数据测试
        addData();

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
        String items[]={ "星期一", "星期二","星期三","星期四","星期五","星期六"};
        for (String strText:items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",strText);
            srcTable.add(map);
        }
        saTable.notifyDataSetChanged(); //更新数据
    }

    public void addData(){

        for(int i=0;i<3;i++){
            String items[]={ "语文", "数学","英语","体育","计算机","化学"};
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





}
