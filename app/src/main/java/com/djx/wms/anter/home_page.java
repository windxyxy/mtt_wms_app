package com.djx.wms.anter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.djx.wms.anter.ReceviePro.MttNotifi;

/**
 * Created by gfgh on 2016/3/10.
 */


public class home_page extends buttom_state {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    /*
    * 測試消息推送功能
    * */
    public void testMessage(View view){
        MttNotifi mttNotifi = new MttNotifi();
        mttNotifi.getNotification(210);
    }

    public void Storageclick(View v) {
        Intent intent = new Intent();/*创建一个新的intent对象*/
        intent.setClass(home_page.this, twolevel_menu.class);
        /*设置Intent的源地址和目标地址*/
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        home_page.this.finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public void Backwhselect(View v) {

        Intent myIntent = new Intent();
        myIntent = new Intent(home_page.this, whselelct.class);
        startActivity(myIntent);
        home_page.this.finish();

    }

    public void test(View v) {

        Intent myIntent = new Intent();
        myIntent = new Intent(home_page.this, order_details.class);
        startActivity(myIntent);
        home_page.this.finish();
    }

    public void library(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(home_page.this, library_management.class);
        startActivity(myIntent);
        home_page.this.finish();
    }


    public void pickingoutbound_menu(View v) {
        Intent intent = new Intent();
        intent.setClass(home_page.this, outbound_menu.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        home_page.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(home_page.this, whselelct.class);
            startActivity(myIntent);
            home_page.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
