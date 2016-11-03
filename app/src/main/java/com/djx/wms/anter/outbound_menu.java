package com.djx.wms.anter;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

/**
 * Created by gfgh on 2016/6/3.
 */
public class outbound_menu extends buttom_state {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outbound_menu);
    }



    public  void  pickingquery(View v){
        Intent intent = new Intent();
        intent.setClass(outbound_menu.this, pickingquery.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        outbound_menu.this.finish();
    }


    public  void clickallocation_task(View v){
        Intent intent = new Intent();
        intent.setClass(outbound_menu.this, allocation_task.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        outbound_menu.this.finish();
    }

    public  void  outbound_menubcak(View v){
        Intent intent = new Intent();
        intent.setClass(outbound_menu.this, home_page.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        outbound_menu.this.finish();
    }




    public  void  click_thelibrary(View v){
        Intent intent = new Intent();
        intent.setClass(outbound_menu.this, the_library.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        outbound_menu.this.finish();
    }








    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(outbound_menu.this, home_page.class);
            startActivity(myIntent);
            outbound_menu.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}
