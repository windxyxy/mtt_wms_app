package com.djx.wms.anter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
 * Created by gfgh on 2016/5/18.
 */
public class transfer_task extends buttom_state {

    private List<Hashtable> listData = new ArrayList<Hashtable>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_task);

        String SQL = "select t_movegoods.*,SUM(c.stock) as stock from (select * from t_movegoods where orderStatus = 1 and whId =" + AppStart.GetInstance().Warehouse + " and mgoType=3 " +
                "union select a.* from t_movegoods as a  join t_movegoods_log as b on a.mgoNo = b.mgoNo  where (a.orderStatus = 4 or a.orderStatus =5) and a.mgoType = 3   and b.userID =" + AppStart.GetInstance().getUserID() + " and a.whId=" + AppStart.GetInstance().Warehouse + ") " +
                "as t_movegoods left join v_fillgoods as c on t_movegoods.mgoNo = c.mgoNo " + " group by " +
                "t_movegoods.indexId,t_movegoods.mgoNo,t_movegoods.whId,t_movegoods.whName,t_movegoods.mgoType,t_movegoods.creator,t_movegoods.auditor,t_movegoods.executor,t_movegoods.createTime,t_movegoods.lastTime,t_movegoods.orderStatus,t_movegoods.flagId,t_movegoods.flagName,t_movegoods.remark,t_movegoods.fillNum";


        listData = Datarequest.GetDataArrayList(SQL);
        if (listData.size() == 0) {
            AlertDialog.Builder build = new AlertDialog.Builder(transfer_task.this);
            build.setMessage("没有任务！").show();
            return;
        }


        LinearLayout veric = (LinearLayout) findViewById(R.id.center);
        for (int i = -1; i < listData.size(); i++) {
            LinearLayout lin = new LinearLayout(this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setGravity(Gravity.CENTER);

            Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);

            if (i == -1) {
                TextView tv_explain = new TextView(this);
                tv_explain.setBackgroundDrawable(statusQuestionDrawables);
                String explain = "移库:指货品在仓库货位之间的移动；<br />" +
                        "具体操作：<br />" +
                        "1、用户点击右上角<font color = '#FF0000'>创建移库单</font>，系统创建移库任务在下方列表中显示。<br />" +
                        "2、用户选择一个移库任务，点击“拣”，进行货品拣货，拣货完成后，再点击“补”，进行货品补货，当任务完成后，该任务从列表中消失。<br />";
                tv_explain.setTextSize(18);
                tv_explain.setText(Html.fromHtml(explain));
                lin.addView(tv_explain);
                veric.addView(lin);
            } else {
                Map taskdata = listData.get(i);

            /*lin.setBackgroundDrawable(statusQuestionDrawables);*/
                TextView Text = new TextView(this);
                Text.setBackgroundDrawable(statusQuestionDrawables);
                Text.setWidth(260);
                Text.setTextSize(20);
                Text.setGravity(Gravity.CENTER);
                Text.setId(i);
                Text.setText(taskdata.get("mgoNo").toString());


                Button btn = new Button(this);
                Drawable btnstatus = getResources().getDrawable(R.drawable.button);
                btn.setBackgroundDrawable(btnstatus);
                btn.setText("拣");
                btn.setWidth(30);
                btn.setHeight(55);
                btn.setId(Integer.parseInt(taskdata.get("indexId").toString()));
            /*获取值*/
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   /*jhPosition*/
                        String status = "";
                        for (Map poscode : listData) {
                            if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                                status = poscode.get("orderStatus").toString();
                            }
                        }
                        if (status.equals("1")) {
                            Hashtable Param = new Hashtable<>();
                            Param.put("SPName", "PRO_MOVESGOODS_LOCK");
                        /* 用户ID*/
                            int userid = AppStart.GetInstance().getUserID();
                            Param.put("userId", "" + userid + "");
                            Param.put("indexId", "" + v.getId() + "");
                            Param.put("mgotype", "3");
                            Param.put("msg", "output-varchar-8000");
                            List<Hashtable> lock = new ArrayList<Hashtable>();
                            lock = Datarequest.GETstored(Param);
                            if (!lock.get(0).get("result").toString().equals("0.0")) {
                                AlertDialog.Builder build = new AlertDialog.Builder(transfer_task.this);
                                build.setMessage(lock.get(0).get("msg").toString()).show();
                                return;
                            }
                        }

                        String order = "";
                        for (Map poscode : listData) {
                            if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                                order = poscode.get("mgoNo").toString();
                            }
                        }


                        Intent intent = new Intent();
                        intent.setClass(transfer_task.this, transfer_pick.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        transfer_task.this.finish();


                    }
                });

                if (taskdata.get("orderStatus").toString().equals("5")) {
                    Drawable disbtns = getResources().getDrawable(R.drawable.disablebutton);
                    btn.setClickable(false);
                    btn.setBackgroundDrawable(disbtns);
                }


                Button btntwo = new Button(this);

                btntwo.setBackgroundDrawable(btnstatus);
                btntwo.setText("补");
                btn.setWidth(30);

                btntwo.setHeight(55);
                btntwo.setId(Integer.parseInt(taskdata.get("indexId").toString()));

            /*获取值*/
                btntwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /*jhPosition*/
                        String order = "";
                        for (Map poscode : listData) {
                            if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                                order = poscode.get("mgoNo").toString();
                            }
                        }

                        Intent intent = new Intent();
                        intent.setClass(transfer_task.this, transfer_repair.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        transfer_task.this.finish();

                 /*   List<Hashtable> gridData = new ArrayList<Hashtable>();
                    String SQL = "select * from v_fillgoods where indexId ='"+v.getId()+"' and whId='"+AppStart.GetInstance().Warehouse+"'";
                    gridData = Datarequest.GetDataArrayList(SQL);
                    if (gridData.size()!= 0) {


                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("griddata", (Serializable) gridData);
                        intent.putExtras(bundle);
                        intent.setClass(transfer_task.this, transfer_repair.class);
                        startActivity(intent);

                    }else {
                        AlertDialog.Builder build = new AlertDialog.Builder(transfer_task.this);
                        build.setMessage("该任务已完成！").show();
                    }
*/

                    }
                });

                if (taskdata.get("stock").toString().equals("")) {
                    Drawable disbtn = getResources().getDrawable(R.drawable.disablebutton);
                    btntwo.setClickable(false);
                    btntwo.setBackgroundDrawable(disbtn);
                }


                lin.addView(Text);
                lin.addView(btn);
                lin.addView(btntwo);
                veric.addView(lin);
            }
        }
    }

    public void create_transfer(View v) {

        Hashtable ParamValue = new Hashtable<>();
        ParamValue.put("SPName", "PRO_MOVESGOODS_ADD");
        ParamValue.put("whid", AppStart.GetInstance().Warehouse);
        int userId = AppStart.GetInstance().getUserID();
        ParamValue.put("userID", "" + userId + "");
        ParamValue.put("mgotype", "3");
        ParamValue.put("inPosFullCode", "");
        ParamValue.put("flagId", "");
        ParamValue.put("remark", "");
        ParamValue.put("msg", "output-varchar-8000");

        List<Hashtable> result = new ArrayList<Hashtable>();
        result = Datarequest.GETstored(ParamValue);

        if (result.get(0).get("result").toString().equals("0.0")) {
            AlertDialog.Builder build = new AlertDialog.Builder(transfer_task.this);
            build.setMessage("生成移库单成功！");
            build.show();

            Intent intent = new Intent();
            intent.setClass(transfer_task.this, transfer_task.class);
            startActivity(intent);
            transfer_task.this.finish();

        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(transfer_task.this);
            build.setMessage(result.get(0).get("msg").toString()).show();
        }

    }


    public void backquery(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(transfer_task.this, library_management.class);
        startActivity(myIntent);
        transfer_task.this.finish();
    }


    /*返回键拦截*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(transfer_task.this, library_management.class);
            startActivity(myIntent);
            transfer_task.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!this.isFinishing()){

        }
    }
}
