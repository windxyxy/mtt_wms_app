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
 * Created by gfgh on 2016/5/23.
 */
public class less_goods extends  buttom_state {

    private  List<Hashtable> listData = new ArrayList<Hashtable>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.less_goods);


        String SQL="select t_movegoods.*,c.stock from (select * from t_movegoods where orderStatus = 1 and whId ="+AppStart.GetInstance().Warehouse+" and mgoType=5 " +
                "union select a.* from t_movegoods as a  join t_movegoods_log as b on a.mgoNo = b.mgoNo  where (a.orderStatus = 4 or a.orderStatus=5) and a.mgoType =5  and b.userID ="+AppStart.GetInstance().getUserID()+" and a.whId="+AppStart.GetInstance().Warehouse+") " +
                "as t_movegoods left join v_fillgoods as c on t_movegoods.mgoNo = c.mgoNo";

        listData = Datarequest.GetDataArrayList(SQL);
        if(listData.size()==0){
            AlertDialog.Builder build = new AlertDialog.Builder(less_goods.this);
            build.setMessage("没有任务！").show();
            return;
        }

        LinearLayout veric=(LinearLayout)findViewById(R.id.center);
        for(int i=0;i<listData.size();i++){
            Map taskdata=listData.get(i);
            LinearLayout lin=new LinearLayout(this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setGravity(Gravity.CENTER);

            Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);
            TextView Text=new TextView(this);
            Text.setBackgroundDrawable(statusQuestionDrawables);

            Text.setWidth(260);
            Text.setTextSize(20);
            Text.setGravity(Gravity.CENTER);
            Text.setId(i);
            Text.setText(taskdata.get("mgoNo").toString());

            Button btn=new Button(this);
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
                    String postion = "";
                    String replenorder = "";
                    for (Map poscode : listData) {
                        if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                            status = poscode.get("orderStatus").toString();
                            postion = poscode.get("remark").toString();
                            replenorder = poscode.get("mgoNo").toString();
                        }
                    }


                    if (status.equals("1")) {
                        Hashtable Param = new Hashtable<>();
                        Param.put("SPName", "PRO_MOVESGOODS_LOCK");
                       /* 用户ID*/
                        int userid = AppStart.GetInstance().getUserID();
                        Param.put("userId", "" + userid + "");
                        Param.put("indexId", "" + v.getId() + "");
                        Param.put("mgotype", "5");
                        Param.put("msg", "output-varchar-8000");
                        List<Hashtable> lock = new ArrayList<Hashtable>();
                        lock = Datarequest.GETstored(Param);
                        if (!lock.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(less_goods.this);
                            build.setMessage(lock.get(0).get("msg").toString()).show();
                            return;
                        }
                    }

                    AppStart.GetInstance().lessorder=replenorder;
                    Intent intent = new Intent();
                    intent.setClass(less_goods.this, lessgood_picking.class);
                    startActivity(intent);
                    less_goods.this.finish();
                }
            });

            /*if(taskdata.get("orderStatus").toString().equals("5")){
                Drawable disbtns = getResources().getDrawable(R.drawable.disablebutton);
                btn.setClickable(false);
                btn.setBackgroundDrawable(disbtns);
            }
            */

            lin.addView(Text);
            lin.addView(btn);
            veric.addView(lin);
        }


    }


     /*返回*/
    public void backexception_menu(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(less_goods.this, exception_menu.class);
        startActivity(myIntent);
        less_goods.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(less_goods.this, exception_menu.class);
            startActivity(myIntent);
            less_goods.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
