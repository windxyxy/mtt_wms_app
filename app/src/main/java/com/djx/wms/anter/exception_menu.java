package com.djx.wms.anter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gfgh on 2016/5/20.
 */
public class exception_menu extends buttom_state {
    private TextView tv_explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exception_menu);

        tv_explain = (TextView) findViewById(R.id.tv_explain);
        String explain = "反拣说明：<br />" +
                "1、指拣货单在二次分拣前被取消；<br />" +
                "2、二次分拣完成后，点击保存时，发现发货单被取消；<br />" +
                "3、称重时发货单被取消，这3类取消的订单已完成拣货，就需要使用货品反拣将已拣的货品放回到货位。<br />" +
                "<font color = '#FF0000'>注意：货品反捡只操作被取消订单的货品，人为多拿的商品不允许在此功能操作。</font>";
        tv_explain.setText(Html.fromHtml(explain));
        tv_explain.setTextSize(18);
    }

/*
    public void  clickmany(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(exception_menu.this, many_goods.class);
        startActivity(myIntent);
        exception_menu.this.finish();
    }*/


     /* 临时上架*/
     public void temporaryShelves(View v){
         Intent intent = new Intent();
         intent.setClass(exception_menu.this,temporary.class);
         startActivity(intent);
         exception_menu.this.finish();
     }

     public void  backlibrary(View v){
         Intent myIntent = new Intent();
         myIntent = new Intent(exception_menu.this, library_management.class);
         startActivity(myIntent);
         exception_menu.this.finish();
     }


/*
 public void  lessclick(View v){
     Intent myIntent = new Intent();
     myIntent = new Intent(exception_menu.this, less_goods.class);
     startActivity(myIntent);
     exception_menu.this.finish();
 }
*/
    public void  counterclick(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(exception_menu.this, reverse_order.class);
        startActivity(myIntent);
        exception_menu.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(exception_menu.this, library_management.class);
            startActivity(myIntent);
            exception_menu.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
