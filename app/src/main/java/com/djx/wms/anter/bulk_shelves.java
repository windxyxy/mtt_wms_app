package com.djx.wms.anter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.djx.wms.anter.R;
import com.djx.wms.anter.*;
import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.DyDictCache;
import com.djx.wms.anter.tools.cache;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/3/15.
 */
public class bulk_shelves extends buttom_state {
    private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();
    private List<Map<String, String>> listDatas= new ArrayList<Map<String, String>>();
    private List<Hashtable> listhas = new ArrayList<Hashtable>();

    private Spinner mySpinner;
    private Spinner  brand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bulk_shelves);

        /*获取货主字典*/
        List<Hashtable> listhasDyDictCache = new ArrayList<>();
        listhasDyDictCache= DyDictCache.instance.GetDyDict(cache.instance.owner);
        /*获取品牌字典*/
        List<Hashtable> brands = new ArrayList<>();
        brands= DyDictCache.instance.GetDyDict(cache.instance.brand);


        List<CItem> lsts = new ArrayList<CItem>();
        brand= (Spinner) super.findViewById(R.id.spinner2);
        for(int i=0;i<brands.size();i++){
            Map top=brands.get(i);
            int s=Integer.parseInt(top.get("DictValue").toString());
            CItem item = new CItem(s, top.get("DictText").toString());
            lsts.add(item);
        }


        //添加到适配器
        ArrayAdapter<CItem> myaAdapters = new ArrayAdapter<CItem>(this, android.R.layout.simple_spinner_item, lsts);
        brand.setAdapter(myaAdapters);
        //点击事件
        brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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












        List<CItem> lst = new ArrayList<CItem>();
        mySpinner= (Spinner) super.findViewById(R.id.spinner);

        for(int i=0;i<listhasDyDictCache.size();i++){
            Map<String,String> top = new HashMap<String,String>();
            top=listhasDyDictCache.get(i);

           /* //cheboxbox绑定数据
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
                        Toast.makeText(getApplicationContext(), "" + buttonView.getId() + "", Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            });
*/

           /* String ok=top.get("ownerName").toString();*/

            int s=Integer.parseInt(top.get("DictValue").toString());
            CItem item = new CItem(s, top.get("DictText").toString());
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

    public void bulkback(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(bulk_shelves.this,twolevel_menu.class);
        startActivity(myIntent);
        bulk_shelves.this.finish();
    }

    public void creatshelf(View v)
    {
        Intent myIntent = new Intent();
        myIntent = new Intent(bulk_shelves.this,autoshelf.class);
        startActivity(myIntent);

    }

}
