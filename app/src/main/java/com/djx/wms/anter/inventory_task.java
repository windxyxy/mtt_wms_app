package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/5/31.
 */
public class inventory_task extends buttom_state {
    private List<Hashtable> tasklist = new ArrayList<Hashtable>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_task);

        String SQL="select top 1 * from v_inventory where orderStatus = 3 and warehouseId = '"+AppStart.GetInstance().Warehouse+"' order by createTime";
        tasklist = Datarequest.GetDataArrayList(SQL);

        if(tasklist.size()==0){
            AlertDialog.Builder build = new AlertDialog.Builder(inventory_task.this);
            build.setMessage("没有盘点任务!").show();
            return;
        }






        LinearLayout veric=(LinearLayout)findViewById(R.id.center);
        for(int i=0;i<tasklist.size();i++){
            Map taskdata=tasklist.get(i);
            LinearLayout lin=new LinearLayout(this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setGravity(Gravity.CENTER);

            Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);
            TextView Text=new TextView(this);
            Text.setBackgroundDrawable(statusQuestionDrawables);
           /* Text.setHeight(55);*/
            Text.setWidth(240);
            Text.setTextSize(20);
            Text.setGravity(Gravity.CENTER);
            Text.setId(i);
            Text.setText(taskdata.get("orderCode").toString());



            Button btn=new Button(this);
            Drawable btnstatus = getResources().getDrawable(R.drawable.button);
            btn.setBackgroundDrawable(btnstatus);
            btn.setText("全盘");
            btn.setTextSize(14);

            btn.setHeight(55);
            btn.setId(Integer.parseInt(taskdata.get("indexId").toString()));
            /*获取值*/
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /*jhPosition*/

                    String orderId = "";
                    String taskorder = "";
                    for (Map poscode : tasklist) {
                        if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                            orderId = poscode.get("indexId").toString();
                            taskorder = poscode.get("orderCode").toString();
                        }
                    }


                    List<Hashtable> gooddetail = new ArrayList<Hashtable>();
                    String SQL = "select  * from v_inventory_detail where orderId = '"+orderId+"' order by routeValue  OFFSET "+AppStart.GetInstance().pdsum+" ROW FETCH NEXT 2 Rows Only; ";
                    gooddetail = Datarequest.GetDataArrayList(SQL);

                    if(gooddetail.size() != 0) {

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.putExtra("status", "1");
                        intent.putExtra("order", taskorder);
                        /*bundle.putSerializable("data", (Serializable) listData);*/
                        bundle.putSerializable("data", (Serializable) gooddetail);
                        intent.putExtras(bundle);
                        intent.setClass(inventory_task.this, inventory_goods.class);
                        startActivity(intent);
                        inventory_task.this.finish();

                    }else {
                        AppStart.GetInstance().pdsum=0;
                        AlertDialog.Builder build = new AlertDialog.Builder(inventory_task.this);
                        build.setMessage("该任务下没有盘点货品！").show();
                    }

                }
            });


            Button btntwo=new Button(this);
            btntwo.setBackgroundDrawable(btnstatus);
            btntwo.setWidth(120);
            btntwo.setText("差异盘点");
            btntwo.setTextSize(14);
            btntwo.setHeight(55);
            btntwo.setId(Integer.parseInt(taskdata.get("indexId").toString()));
            /*获取值*/
            btntwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*jhPosition*/
                    String orderId = "";
                    String taskorder = "";
                    for (Map poscode : tasklist) {
                        if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                            orderId = poscode.get("indexId").toString();
                            taskorder = poscode.get("orderCode").toString();
                        }
                    }


                    List<Hashtable> gooddetail = new ArrayList<Hashtable>();
                    String SQL = "select  * from v_inventory_detail where orderId = '"+orderId+"' and beforeCheckNum <> lastCheckNum and checkTimes > 0 order by routeValue  OFFSET "+AppStart.GetInstance().cysum+" ROW FETCH NEXT 2 Rows Only; ";
                    gooddetail = Datarequest.GetDataArrayList(SQL);
                    if (gooddetail.size() != 0) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.putExtra("status", "2");
                        intent.putExtra("order", taskorder);
                       /*       bundle.putSerializable("data", (Serializable) listData);*/
                        bundle.putSerializable("data", (Serializable) gooddetail);
                        intent.putExtras(bundle);
                        intent.setClass(inventory_task.this, inventory_goods.class);
                        startActivity(intent);
                        inventory_task.this.finish();
                    }else {
                        AlertDialog.Builder build = new AlertDialog.Builder(inventory_task.this);
                        build.setMessage("该任务下没有盘点货品！").show();
                    }

                }
            });


            lin.addView(Text);
            lin.addView(btn);
            lin.addView(btntwo);
            veric.addView(lin);
        }



    }



    /*返回*/
    public void backlibrary_management(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(inventory_task.this, library_management.class);
        startActivity(myIntent);
        inventory_task.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(inventory_task.this, library_management.class);
            startActivity(myIntent);
            inventory_task.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
