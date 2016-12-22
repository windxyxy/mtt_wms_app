package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class PickingAreaUp extends buttom_state {
    private TextView tv_explain;
    private List<Hashtable> mList = new ArrayList<>();
    private List<Hashtable> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking_area_up);
        tv_explain = (TextView) findViewById(R.id.tv_explain);
        String SQL = "select t_movegoods.*,c.stock from " +
                "(select * from t_movegoods where orderStatus = 1 and whId = " + AppStart.GetInstance().Warehouse + " and mgoType = 7 " +
                " union " +
                "select a.* from t_movegoods as a " +
                "join t_movegoods_log as b on a.mgoNo = b.mgoNo  where (a.orderStatus = 4 or a.orderStatus = 5) and a.mgoType = 7 " +
                "and b.userID = " + AppStart.GetInstance().getUserID() + " and a.whId = " + AppStart.GetInstance().Warehouse + ") as t_movegoods " +
                "left join v_fillgoods as c on t_movegoods.mgoNo = c.mgoNo ";
        mList = Datarequest.GetDataArrayList(SQL);
        if (mList.size() == 0) {
            new AlertDialog.Builder(PickingAreaUp.this)
                    .setMessage("没有任务")
                    .show();
            return;
        }
        LinearLayout ll_mission = (LinearLayout) findViewById(R.id.ll_mission);

        for (int i = 0; i < mList.size(); i++) {
            LinearLayout ll = new LinearLayout(PickingAreaUp.this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER);
            Drawable tv_bg = getResources().getDrawable(R.drawable.shape_tvbg);

            final Map taskData = mList.get(i);
            final TextView tv_mgoNo = new TextView(PickingAreaUp.this);
            tv_mgoNo.setBackgroundDrawable(tv_bg);
            tv_mgoNo.setWidth(230);
            tv_mgoNo.setTextSize(20);
            tv_mgoNo.setGravity(Gravity.CENTER);
            tv_mgoNo.setId(i);
            tv_mgoNo.setText(taskData.get("mgoNo").toString());

            Button btn_pick = new Button(PickingAreaUp.this);
            Drawable btn_bg = getResources().getDrawable(R.drawable.button);
            btn_pick.setBackgroundDrawable(btn_bg);
            btn_pick.setText("拣");
            btn_pick.setWidth(30);
            btn_pick.setHeight(55);
            btn_pick.setId(Integer.parseInt(taskData.get("indexId").toString()));
            btn_pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String wareGoodscode = taskData.get("remark").toString();
                    String SQL = "select * from v_stock where whAareType = 'BH' and warehouseId = " + AppStart.GetInstance().Warehouse + " and  wareGoodsCodes = '" + wareGoodscode + "'";
//                    String SQL1 = "select * from v_stock where (whAareType = 'BH' or whAareType = 'SH') and warehouseId = " + AppStart.GetInstance().Warehouse + " and  wareGoodsCodes = '" + wareGoodscode + "'";
                    lists = new ArrayList<Hashtable>();
                    lists = Datarequest.GetDataArrayList(SQL);
                    if (lists.size() == 0){
                        new AlertDialog.Builder(PickingAreaUp.this)
                                .setTitle("提示")
                                .setMessage("　　"+wareGoodscode+"\n　　备货区没有该商品")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return;
                    }
//                    if (lists.size() == 0) {
//                        Toast.makeText(PickingAreaUp.this, "没有查询到商品信息", Toast.LENGTH_SHORT).show();
//                        return;
//                    } else {
//                        String wgc = lists.get(0).get("wareGoodsCode").toString();
//                        String SQL = "select * from v_stock where whAareType = 'BH' and warehouseId = " + AppStart.GetInstance().Warehouse + " and  wareGoodsCodes = '" + wareGoodscode + "'";
//                        List<Hashtable> list = new ArrayList<Hashtable>();
//                        list = Datarequest.GetDataArrayList(SQL);
//                        if (list.size() == 0) {
//                            new AlertDialog.Builder(PickingAreaUp.this)
//                                    .setTitle("提示")
//                                    .setMessage("备货区没有该商品-"+wgc)
//                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    })
//                                    .show();
//                        }
//                    }
                    String pickOrder = TransactSQL.instance.filterSQL(tv_mgoNo.getText().toString());
                    Intent intent = new Intent(PickingAreaUp.this, PickingUp.class);
                    intent.putExtra("pickOrder", pickOrder);
                    intent.putExtra("wareGoodscode", wareGoodscode);
                    startActivity(intent);
                    PickingAreaUp.this.finish();
//                    }
                }
            });

            if (taskData.get("orderStatus").toString().equals("5")) {
                Drawable btnbg = getResources().getDrawable(R.drawable.disablebutton);
                btn_pick.setClickable(false);
                btn_pick.setBackgroundDrawable(btnbg);
            }

            Button btn_repair = new Button(PickingAreaUp.this);
            btn_repair.setBackgroundDrawable(btn_bg);
            btn_repair.setText("补");
            btn_repair.setWidth(30);
            btn_repair.setHeight(55);
            btn_repair.setId(i);
            btn_repair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String pickOrder = TransactSQL.instance.filterSQL(tv_mgoNo.getText().toString());
                    Intent intent = new Intent(PickingAreaUp.this, PickingRepair.class);
                    intent.putExtra("pickOrder", pickOrder);
                    String stock = taskData.get("stock").toString();

                    if (stock.equals("0") || stock.equals("")) {
                        new AlertDialog.Builder(PickingAreaUp.this)
                                .setMessage("请先拣货")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                    intent.putExtra("stock", stock);
                    String wareGoodscode = taskData.get("remark").toString();
                    intent.putExtra("wareGoodscode", wareGoodscode);
//                        intent.putExtras(bundle);
                    startActivity(intent);
                    PickingAreaUp.this.finish();
                }

//                }
            });

            if (taskData.get("orderStatus").toString().equals("1")) {
                Drawable btnbg = getResources().getDrawable(R.drawable.disablebutton);
                btn_repair.setClickable(false);
                btn_repair.setBackgroundDrawable(btnbg);
            }

            ll.addView(tv_mgoNo);
            ll.addView(btn_pick);
            ll.addView(btn_repair);
            ll_mission.addView(ll);

            if (taskData.get("orderStatus").toString().equals("8")) {
                ll_mission.removeView(ll);
            }

        }
    }


    public void backToHome(View view) {
        Intent intent = new Intent(PickingAreaUp.this, library_management.class);
        startActivity(intent);
        PickingAreaUp.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(PickingAreaUp.this, library_management.class);
            startActivity(intent);
            PickingAreaUp.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
