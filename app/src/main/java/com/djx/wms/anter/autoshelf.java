package com.djx.wms.anter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by gfgh on 2016/3/22.
 */
public class autoshelf extends buttom_state {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoshelf);
    }


    public void autoshelfback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(autoshelf.this,bulk_shelves.class);
        startActivity(myIntent);
        autoshelf.this.finish();
    }


}
