package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
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
 * Created by gfgh on 2016/4/21.
 */
public class reple_task extends buttom_state {


    /*private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();*/
    private List<Hashtable> listData = new ArrayList<Hashtable>();
    private String jhPosition;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reple_task);
        List<Hashtable> listhas = new ArrayList<Hashtable>();

        String SQL = "select t_movegoods.*,c.stock from (select *,dbo.getPositionId(t_movegoods.whId,t_movegoods.remark) as positionId from t_movegoods where orderStatus = 1" +
                " and whId =" + AppStart.GetInstance().Warehouse + " and mgoType=2 union select a.*,dbo.getPositionId(a.whId,a.remark) as positionId from t_movegoods as a  join t_movegoods_log as b on a.mgoNo = b.mgoNo  where (a.orderStatus = 4 or a.orderStatus=5) and a.mgoType = 2" +
                " and b.userID=" + AppStart.GetInstance().getUserID() + " and a.whId=" + AppStart.GetInstance().Warehouse + " ) as t_movegoods left join v_fillgoods as c on t_movegoods.mgoNo = c.mgoNo left join t_position as d on t_movegoods.positionId = d.indexId order by routeValue,remark";

        listData = Datarequest.GetDataArrayList(SQL);
        if (listData.size() == 0) {
            AlertDialog.Builder build = new AlertDialog.Builder(reple_task.this);
            build.setMessage("没有补货任务！").show();
            return;
        }


        LinearLayout veric = (LinearLayout) findViewById(R.id.center);
        for (int i = 0; i < listData.size(); i++) {
            Map taskdata = listData.get(i);
            LinearLayout lin = new LinearLayout(this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setGravity(Gravity.CENTER);

            Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);
            /*lin.setBackgroundDrawable(statusQuestionDrawables);*/

            TextView Text = new TextView(this);
            Text.setBackgroundDrawable(statusQuestionDrawables);
            Text.setWidth(260);
            Text.setTextSize(20);
            Text.setGravity(Gravity.CENTER);
            Text.setId(i);
            Text.setText(taskdata.get("remark").toString());

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
                        Param.put("mgotype", "2");
                        Param.put("msg", "output-varchar-8000");
                        List<Hashtable> lock = new ArrayList<Hashtable>();
                        lock = Datarequest.GETstored(Param);
                        if (!lock.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(reple_task.this);
                            build.setMessage(lock.get(0).get("msg").toString()).show();
                            return;
                        }
                    }


                    List<Hashtable> wareCode = new ArrayList<Hashtable>();
                    String SQL = "select * from v_stock  where  whAareType='JH' and warehouseId='" + AppStart.GetInstance().Warehouse + "' and posCode='" + postion + "'";
                    wareCode = Datarequest.GetDataArrayList(SQL);

                    List<Hashtable> gridData = new ArrayList<Hashtable>();
                    String SQLS = "select * from v_stock  where  wareGoodsCodes='" + wareCode.get(0).get("wareGoodsCodes").toString() + "' and warehouseId='" + AppStart.GetInstance().Warehouse + "' and whAareType='BH' and realStock!=0";
                    gridData = Datarequest.GetDataArrayList(SQLS);
                    if (gridData.size() != 0) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.putExtra("order", replenorder);
                       /*bundle.putSerializable("data", (Serializable) listData);*/
                        bundle.putSerializable("griddata", (Serializable) gridData);
                        intent.putExtras(bundle);
                        intent.setClass(reple_task.this, reple_goods.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder build = new AlertDialog.Builder(reple_task.this);
                        build.setMessage("备货区下没有库存！").show();
                    }


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
                    String replenorder = "";
                    String postion = "";
                    for (Map poscode : listData) {
                        if (poscode.get("indexId").toString().equals("" + v.getId() + "")) {
                            replenorder = poscode.get("mgoNo").toString();
                            postion = poscode.get("remark").toString();
                        }
                    }

                    List<Hashtable> gridData = new ArrayList<Hashtable>();
                    String SQL = "select *from t_movegoods_detail where mgoNo='" + replenorder + "'";
                    gridData = Datarequest.GetDataArrayList(SQL);

                    if (gridData.size() != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("order", replenorder);
                        intent.putExtra("jhpostion", postion);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("griddata", (Serializable) gridData);
                        intent.putExtras(bundle);
                        intent.setClass(reple_task.this, reple_replenishment.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder build = new AlertDialog.Builder(reple_task.this);
                        build.setMessage("请拣货！").show();
                    }

                    /*AlertDialog.Builder build = new AlertDialog.Builder(reple_task.this);
                    build.setMessage("" + v.getId() + "").show();*/
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


    public void backquery(View v) {
        Intent intent = new Intent();
        intent.setClass(reple_task.this, replenishment_new.class);
        startActivity(intent);
        reple_task.this.finish();
    }

    public void clickgood(View v) {
        Intent intent = new Intent();
        intent.setClass(reple_task.this, reple_singlegood.class);
        startActivity(intent);
        reple_task.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(reple_task.this, replenishment_new.class);
            startActivity(myIntent);
            reple_task.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
