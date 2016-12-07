package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.djx.wms.anter.entity.RoleInfor;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.DyDictCache;
import com.djx.wms.anter.tools.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import pers.lh.communication.TranCoreClass;

/**
 * Created by gfgh on 2016/4/8.
 */
public class whselelct extends buttom_state {

    private Spinner brand;
    /*spinner 初始化下标状态*/
    private Boolean warestaus = true;
    private List<Hashtable> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whselect);
        /*
        *
        * */


        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            if (AppStart.GetInstance().GetCommunicationConn().Connector.GetTid().equals("")) {
            /*  Intent intent=new Intent();
                intent.setClass(whselelct.this, whselelct.class);
                        *//*设置Intent的源地址和目标地址*//*
                startActivity(intent);*//*调用startActivity方法发送意图给系统*/
                finish();
            }

        }



        /*获取仓库字典*/
        List<Hashtable> brands = new ArrayList<>();
        brands = DyDictCache.instance.GetDyDict(cache.instance.warehouse);

        List<CItem> lsts = new ArrayList<CItem>();
        brand = (Spinner) super.findViewById(R.id.spinner4);

        /* 权限仓库管理*/
        String[] stringArr = AppStart.GetInstance().getWarehouseIDs().split(",");
        RoleInfor RoleInforS = AppStart.GetInstance().getRoleInfor();
        for (int i = 0; i < brands.size(); i++) {
            Map top = brands.get(i);
            /*超级管理员仓库*/
            if (RoleInforS.getisSuper()) {
                int s = Integer.parseInt(top.get("DictValue").toString());
                CItem item = new CItem(s, top.get("DictText").toString());
                lsts.add(item);
            } else {

                /*用户分配权限*/
                for (String whid : stringArr) {
                    if (top.get("DictValue").toString().equals(whid)) {
                        int s = Integer.parseInt(top.get("DictValue").toString());
                        CItem item = new CItem(s, top.get("DictText").toString());
                        lsts.add(item);
                    }
                }


            }


        }


        //添加到适配器
        ArrayAdapter<CItem> myaAdapters = new ArrayAdapter<CItem>(this, android.R.layout.simple_spinner_item, lsts);
        brand.setAdapter(myaAdapters);
        //点击事件
        brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /** ids是刚刚新建的list里面的ID*/
                int ids = ((CItem) brand.getSelectedItem()).GetID();
                System.out.println(ids);
                AppStart.GetInstance().Warehouse = ids;

                /*仓库缓存，下标处理*/
                if (warestaus) {
                    warestaus = false;
                    brand.setSelection(AppStart.GetInstance().waresubscript, true);
                } else {
                    AppStart.GetInstance().waresubscript = position;
                    brand.setSelection(AppStart.GetInstance().waresubscript, true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        if (AppStart.GetInstance().GetCommunicationConn().state) {
            Intent intent = new Intent("commnication.status");
            intent.putExtra("status", 200);
            AnterService.GetServiceInstans().sendBroadcast(intent);
        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
        /*    new android.support.v7.app.AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("确定要脱出吗？")  .setView(
                    new EditText(this)).setPositiveButton("确定", null)
                    .setNegativeButton("取消", null).show();*/
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定退出登录吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            AppStart.GetInstance().setUserconfig("", "");
                            AppStart.GetInstance().initUserEntity();
                            AppStart.GetInstance().GetCommunicationConn().state = false;
                            Boolean tranCoreClass;
                            AppStart.GetInstance().GetCommunicationConn().Connector.SetTid("");
                            tranCoreClass = (Boolean) (AppStart.GetInstance().GetCommunicationConn().AsyncSend(4, 1));

                            if (tranCoreClass) {
                                Intent myIntent = new Intent();
                                myIntent = new Intent(whselelct.this, main_login.class);
                                startActivity(myIntent);
                            } else {
                                AlertDialog.Builder build = new AlertDialog.Builder(whselelct.this);
                                build.setMessage("退出失败").show();
                            }
                            /*AppStart.GetInstance().GetCommunicationConn().Connector.Close();*/
                            /*AppStart.GetInstance().SetCommunication(null);*/
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            /*this.finish();*/
                        }
                    })

                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backmeuns(View view) {

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定退出登录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        AppStart.GetInstance().setUserconfig("", "");
                        AppStart.GetInstance().initUserEntity();

                        AppStart.GetInstance().GetCommunicationConn().state = false;
                        Boolean tranCoreClass;
                        AppStart.GetInstance().GetCommunicationConn().Connector.SetTid("");
                        tranCoreClass = (Boolean) (AppStart.GetInstance().GetCommunicationConn().AsyncSend(4, 1));

                        if (tranCoreClass) {
                            Intent myIntent = new Intent();
                            myIntent = new Intent(whselelct.this, main_login.class);
                            startActivity(myIntent);
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(whselelct.this);
                            build.setMessage("退出失败").show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*this.finish();*/
                    }
                })
                .show();

    }


    public void Clickhome(View v) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您将操作该仓库信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Hashtable parm = new Hashtable<>();
                        parm.put("SPName","sp_login_user_warehouse");
                        parm.put("userId",AppStart.GetInstance().getUserID());
                        parm.put("warehouseId",AppStart.GetInstance().Warehouse+"");
                        parm.put("msg","output-varchar-500");
                        mList = Datarequest.GETstored(parm);
                        if (mList.get(0).get("result").equals("0.0")){
                            Log.e("whselect",mList.get(0).toString());
                        }else {
                            Log.e("anter",mList.get(0).get("msg").toString());
                        }
                        Intent myIntent = new Intent();
                        myIntent = new Intent(whselelct.this, home_page.class);
                        startActivity(myIntent);
                    }
                })

                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


}
