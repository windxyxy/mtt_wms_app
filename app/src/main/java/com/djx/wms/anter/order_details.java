package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.djx.wms.anter.tools.Datarequest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/3/14.
 */
public class order_details extends buttom_state {
  public Spinner mySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);


        mySpinner= (Spinner) super.findViewById(R.id.spinner3);
        Hashtable<String, String> maps = new Hashtable<String, String>();

        maps.put("1","测试值");
        maps.put("2","测试值");
        maps.put("3", "测试值");
        maps.put("4", "测试值");

        AlertDialog.Builder build = new AlertDialog.Builder(order_details.this);
        build.setMessage(""+maps.get(1)+"").show();

        List<CItem> lst = new ArrayList<CItem>();

        List<Hashtable> listhas = new ArrayList<Hashtable>();
        String SQL="select *from v_purchase_android";
        listhas= Datarequest.GetDataArrayList(SQL);


        LinearLayout ll = (LinearLayout)findViewById(R.id.che);








        for(int i=0;i<listhas.size();i++){
            Map top=listhas.get(i);

            //cheboxbox绑定数据
            CheckBox checkbox = new CheckBox(order_details.this);

            int ID= Integer.parseInt(top.get("IndexID").toString());
            checkbox.setId(ID);
            checkbox.setText(top.get("goodsName").toString());
            ll.addView(checkbox);
            //绑定事件
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                     boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(),""+buttonView.getId()+"", Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            });



            int s=Integer.parseInt(top.get("IndexID").toString());

            CItem item = new CItem(s, top.get("goodsName").toString());

            lst.add(item);

        }


        //添加到适配器
        ArrayAdapter<CItem> myaAdapter = new ArrayAdapter<CItem>(this, android.R.layout.simple_spinner_item, lst);
        mySpinner.setAdapter(myaAdapter);

        //点击事件
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            /*
             * ids是刚刚新建的list里面的ID
             */
                int ids = ((CItem) mySpinner.getSelectedItem()).GetID();
                System.out.println(ids);
                Toast.makeText(getApplicationContext(), String.valueOf(ids), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void detailsback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(order_details.this, shelf_list.class);
        startActivity(myIntent);
    }

}
