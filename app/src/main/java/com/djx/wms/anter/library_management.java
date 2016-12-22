package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.djx.wms.anter.R;
import com.djx.wms.anter.buttom_state;
import com.djx.wms.anter.tools.Datarequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by gfgh on 2016/4/5.
 */
public class library_management extends buttom_state {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_management);
    }


    public void replenishment(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, replenishment_new.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/

    }

    public void library_managementbcak(View v) {

        Intent intent = new Intent();
        intent.setClass(library_management.this, home_page.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    public void transfer_click(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, transfer_task.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    /*异常处理*/
    public void exception_menu(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, exception_menu.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    /* 货位查询*/
    public void clickpostionquery(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, query_library.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    /* 货品查询*/
    public void clickgoodquery(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, goods_find.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    /*临时盘点*/
    public void temporaryInventory(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, query_good.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();
    }

    /*盘点*/
    public void clickinventory_task(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, temporary_lnventory.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        library_management.this.finish();

    }

    public void clicklibrary_processing(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, library_processing.class);
        startActivity(intent);
        library_management.this.finish();

    }

    /* 下架*/
    public void click_Offshelf(View v) {
        Intent intent = new Intent();
        intent.setClass(library_management.this, off_the_shelf.class);
        startActivity(intent);
        library_management.this.finish();
    }
    /*拣货区上架*/
    public void pickingAreaUp(View view){
        Intent intent = new Intent();
        intent.setClass(library_management.this,PickingAreaUp.class);
        startActivity(intent);
        library_management.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(library_management.this, home_page.class);
            startActivity(myIntent);
            library_management.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
