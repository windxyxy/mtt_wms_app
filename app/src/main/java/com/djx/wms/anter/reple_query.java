package com.djx.wms.anter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/4/6.
 */
public class reple_query extends buttom_state {


    private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reple_query);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        listData= (List) bundle.getSerializable("data");


        RelativeLayout reple_main=(RelativeLayout)findViewById(R.id.reple_main);
        reple_main.setGravity(Gravity.CENTER);


        LinearLayout vereticalhors=new LinearLayout(this);
        vereticalhors.setOrientation(LinearLayout.VERTICAL);


for(Map data:listData){


    LinearLayout hors=new LinearLayout(this);
    hors.setOrientation(LinearLayout.HORIZONTAL);

    TextView texttwo=new TextView(this);
    texttwo.setText(data.get("mgoNo").toString());
    texttwo.setWidth(350);
    texttwo.setHeight(50);
    texttwo.setGravity(Gravity.CENTER);
    Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);
    texttwo.setBackgroundDrawable(statusQuestionDrawables);
    texttwo.setTextSize(20);



    Button btn=new Button(this);
    btn.setText("补货");
    btn.setWidth(100);
    btn.setHeight(35);
    btn.setGravity(Gravity.CENTER);
    Drawable statusQues = getResources().getDrawable(R.drawable.button);
    btn.setBackgroundDrawable(statusQues);
    btn.setTextSize(18);



    hors.addView(texttwo);
    hors.addView(btn);
    vereticalhors.addView(hors);
   }

        reple_main.addView(vereticalhors);
    }

    public  void  reple_querybacks(View v){
        Intent intent = new Intent();
        intent.setClass(reple_query.this, replenishment_new.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
    }

}
