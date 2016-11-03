package com.djx.wms.anter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gfgh on 2016/3/11.
 */
public class shelf_list extends buttom_state {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.shelf_list);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

    }



    public void shelfback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(shelf_list.this, twolevel_menu.class);
        startActivity(myIntent);
    }


    public void determine(View v){

        Intent myIntent = new Intent();
        myIntent = new Intent(shelf_list.this, order_details.class);
        startActivity(myIntent);

    }

}
