package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;

/**
 * Created by gfgh on 2016/6/22.
 */
public class the_library extends buttom_state {


    String SQL = "select * from t_out_stock where warehouseCode = (select whCode from t_warehouse where indexId = " + AppStart.GetInstance().Warehouse + ") " +
            "and orderStatus = 3 and srcWarehouse != warehouseCode " +
            "and outStockNo in (select outStockNo from t_out_stock_log where (operating = '出库手持开始拣货' and userID = " + AppStart.GetInstance().getUserID() + ") or operating = '审核')";

//"and outStockNo in (select outStockNo from t_out_stock_log where operating != '审核' and userID = " + AppStart.GetInstance().getUserID() + ")";

    String SQL2 = "select * from t_out_stock where  warehouseCode = (select whCode from t_warehouse where indexId = " + AppStart.GetInstance().Warehouse + ") and orderStatus = 3 and srcWarehouse != warehouseCode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_library);

        List<Hashtable> ckListData = new ArrayList<>();
        ckListData = Datarequest.GetDataArrayList(SQL);

        if (ckListData.size() == 0) {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setMessage("没有出库单任务！").show();
            return;
        }

        LinearLayout ll_msg = (LinearLayout) findViewById(R.id.ll_msg);
        int realNum = ckListData.size();
        for (int i = 0; i < realNum; i++) {
            final Hashtable ckNo = ckListData.get(i);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER);

            Drawable textBackground = getResources().getDrawable(R.drawable.gridborder);
            final TextView numTv = new TextView(this);
            numTv.setBackgroundDrawable(textBackground);
            numTv.setWidth(300);
            numTv.setTextSize(20);
            numTv.setHeight(50);
            numTv.setGravity(Gravity.CENTER);
            numTv.setId(i);
            numTv.setText(ckNo.get("outStockNo").toString());

            final Button btn = new Button(this);
            Drawable btnBackground = getResources().getDrawable(R.drawable.button);
            btn.setBackgroundDrawable(btnBackground);
            btn.setText("拣");
            btn.setId(numTv.getId());
            btn.setWidth(60);
            btn.setHeight(50);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String pickingorder = numTv.getText().toString();
                    pickingorder = TransactSQL.instance.filterSQL(pickingorder);
                    Intent intent = new Intent();
                    intent.setClass(the_library.this, out_picking.class);
                    intent.putExtra("picorder",pickingorder);
                    startActivity(intent);
                    the_library.this.finish();

//                    Hashtable Param = new Hashtable<>();
//                    Param.put("SPName", "sp_out_stock_detail_get");
//                    Param.put("outstockno ", pickingorder);
//                    int userid = AppStart.GetInstance().getUserID();
//                    Param.put("currid", "" + userid + "");
//                    Param.put("whid", "" + AppStart.GetInstance().Warehouse + "");
//                    Param.put("msg", "output-varchar-500");
//                    List<Hashtable> querypick;
//                    querypick = Datarequest.GETstored(Param);
//                    if (querypick.get(0).get("result").toString().equals("0.0")) {
//                        querypick.remove(0);
//                        AppStart.GetInstance().outorder = pickingorder;
//                        Intent intent = new Intent();
//                        intent.setClass(the_library.this, out_picking.class);
//                        startActivity(intent);
//                        the_library.this.finish();
//                    } else {
//                        new AlertDialog.Builder(the_library.this)
//                                .setTitle("提示")
//                                .setMessage(querypick.get(0).get("msg").toString())
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .show();
//                    }

                }
            });

            ll.addView(numTv);
            ll.addView(btn);
            ll_msg.addView(ll);

        }


//        ckorder = (EditText) findViewById(R.id.editText7);

//        ckorder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    clickoutpick(v);
//                    selectall(ckorder);
//                    return true;
//                }
//                return true;
//            }
//        });


    }

//    public void clickoutpick(View v) {
//
//        String pickingorder = ckorder.getText().toString();
//        pickingorder = TransactSQL.instance.filterSQL(pickingorder);
//
//        if (pickingorder.equals("")) {
//            ckorder.setError("请输入单号！");
//            return;
//        }
//
//        Hashtable Param = new Hashtable<>();
//        Param.put("SPName", "sp_out_stock_detail_get");
//        Param.put("outstockno ", pickingorder);
//        int userid = AppStart.GetInstance().getUserID();
//        Param.put("currid", "" + userid + "");
//        Param.put("whid", "" + AppStart.GetInstance().Warehouse + "");
//        Param.put("msg", "output-varchar-500");
//        List<Hashtable> querypick = new ArrayList<Hashtable>();
//        querypick = Datarequest.GETstored(Param);
//
//
//        if (querypick.get(0).get("result").toString().equals("0.0")) {
//            querypick.remove(0);
//            AppStart.GetInstance().outorder = pickingorder;
//
//
//            Intent intent = new Intent();
//           /*    Bundle bundle = new Bundle();
//            intent.putExtra("order", pickingorder);
//            bundle.putSerializable("listdatas", (Serializable)querypick);
//
//            intent.putExtras(bundle);*/
//            intent.setClass(the_library.this, out_picking.class);
//            startActivity(intent);/*调用startActivity方法发送意图给系统*/
//            the_library.this.finish();
//
//        } else {
//          /*  AlertDialog.Builder build = new AlertDialog.Builder(the_library.this);
//            build.setMessage(querypick.get(0).get("msg").toString()).show();*/
//
//            ckorder.setError(querypick.get(0).get("msg").toString());
//            selectall(ckorder);
//        }
//
//
//    }

    public void the_libraryback(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(the_library.this, outbound_menu.class);
        startActivity(myIntent);
        the_library.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(the_library.this, outbound_menu.class);
            startActivity(myIntent);
            the_library.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
