package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/5/31.
 */
public class library_processing extends  buttom_state {

    private   List<Hashtable> listData = new ArrayList<Hashtable>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_processing);

        List<Hashtable> listhas = new ArrayList<Hashtable>();
        String SQL="select * from t_out_stock where orderStatus =3  and orderType='QTCK' union" +
                " select a.* from t_out_stock as a join t_out_stock_log as b on a.outStockNo= b.outStockNo  where a.orderStatus = 4 and  a.orderType='QTCK'  and b.userID ="+AppStart.GetInstance().getUserID()+"";

        listData = Datarequest.GetDataArrayList(SQL);
        if(listData.size()==0){
            AlertDialog.Builder build = new AlertDialog.Builder(library_processing.this);
            build.setMessage("没有出库任务！").show();
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

            Text.setHeight(55);
            Text.setWidth(260);
            Text.setTextSize(20);
            Text.setGravity(Gravity.CENTER);
            Text.setId(i);
            Text.setText(taskdata.get("outStockNo").toString());




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
                    String Jgorder = "";
                    for (Map poscode : listData) {
                        if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                            status = poscode.get("orderStatus").toString();
                            Jgorder = poscode.get("outStockNo").toString();
                        }
                    }


                    if (status.equals("3")) {
                        Hashtable Param = new Hashtable<>();
                        Param.put("SPName", "PRO_STOREPROCESS_LOCK");
                        /*用户ID*/
                        int userid = AppStart.GetInstance().getUserID();
                        Param.put("userId", "" + userid + "");
                        Param.put("indexId", "" + v.getId() + "");
                        Param.put("msg", "output-varchar-8000");
                        List<Hashtable> lock = new ArrayList<Hashtable>();
                        lock = Datarequest.GETstored(Param);
                        if (!lock.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(library_processing.this);
                            build.setMessage(lock.get(0).get("msg").toString()).show();
                            return;
                        }
                    }

                    AppStart.GetInstance().Jgorder=Jgorder;
                    Intent intent = new Intent();
                    intent.setClass(library_processing.this, library_picking_stock.class);
                    startActivity(intent);
                    library_processing.this.finish();
                }
            });

            /*if(taskdata.get("orderStatus").toString().equals("5")){
                Drawable disbtns = getResources().getDrawable(R.drawable.disablebutton);
                btn.setClickable(false);
                btn.setBackgroundDrawable(disbtns);
            }*/

            lin.addView(Text);
            lin.addView(btn);
            veric.addView(lin);
        }

    }






    /*返回*/
    public void backlibrary_managements(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(library_processing.this, library_management.class);
        startActivity(myIntent);
        library_processing.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(library_processing.this, library_management.class);
            startActivity(myIntent);
            library_processing.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
