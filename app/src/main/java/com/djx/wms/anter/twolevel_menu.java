package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by gfgh on 2016/3/11.
 */
public class twolevel_menu  extends buttom_state {
    private List<Hashtable> listhas = new ArrayList<Hashtable>();

    private Context context = this;

    private TextView tv_explain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twolevel_menu);

        tv_explain = (TextView) findViewById(R.id.tv_explain);
        String explain = "货品上架：<br />货品上架指货品入库到<font color = '#FF0000'>收货区</font>后，需要将货品移入到<font color = '#FF0000'>备货区</font>或者<font color = '#FF0000'>拣货区</font>，避免收货区货品积压。";
        tv_explain.setText(Html.fromHtml(explain));
        tv_explain.setTextSize(18);
    }

    public void twoback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(twolevel_menu.this, home_page.class);
        startActivity(myIntent);

        twolevel_menu.this.finish();
    }

    public void shelfclick(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(twolevel_menu.this, storage_inquiry.class);
        startActivity(myIntent);
        twolevel_menu.this.finish();

    }

  /*  public void buikclick(View v)
    {

        Intent intent = new Intent();
        intent.setClass(twolevel_menu.this,bulk_shelves.class);
        startActivity(intent);*//*调用startActivity方法发送意图给系统*//*
        twolevel_menu.this.finish();
    }
*/


    /* 临时上架
    public void temporaryShelves(View v){
        Intent intent = new Intent();
        intent.setClass(twolevel_menu.this,temporary.class);
        startActivity(intent);
        twolevel_menu.this.finish();
    }
*/

    /*跳转临时盘点
    public void clicktemporary_lnventory(View v){
        Intent intent = new Intent();
        intent.setClass(twolevel_menu.this,temporary_lnventory.class);
        startActivity(intent);
        twolevel_menu.this.finish();
    }*/



//    public void Storageclick(View v)
//    {
//
//
//        String SQL="select isUse from t_config_overall where indexId = 6 and isDelete = 0";
//        listhas= Datarequest.GetDataArrayList(SQL);
//        if(listhas.get(0).get("isUse").toString().equals("1")){
//            AlertDialog.Builder build = new AlertDialog.Builder(twolevel_menu.this);
//            build.setMessage("当前只允许PC端入库！").show();
//            return;
//        }
//
//
//        Intent myIntent = new Intent();
//        myIntent = new Intent(twolevel_menu.this,storage_enquiry.class);
//        startActivity(myIntent);
//        twolevel_menu.this.finish();
//
//    }

   public void temporary_shelves(View v){

                       Intent intent = new Intent();
                       intent = new Intent(twolevel_menu.this,temporary_shelves.class);
                       startActivity(intent);
                       twolevel_menu.this.finish();
   }











    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(twolevel_menu.this, home_page.class);
            startActivity(myIntent);
            twolevel_menu.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
