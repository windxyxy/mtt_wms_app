package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by gfgh on 2016/5/18.
 */
public class transfer_pick extends buttom_state {


    private String order = "", nextgoods = "";
    private int sum = 0, bhsum = 0, odd = 0;
    private TextView orderinput, goodsname;
    private EditText postion, waregood, plQysum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_pick);


        Intent intent = getIntent();
        order = intent.getStringExtra("order");
        orderinput = (TextView) findViewById(R.id.editText56);
        orderinput.setText(order);


        postion = (EditText) findViewById(R.id.editText45);
        waregood = (EditText) findViewById(R.id.editText46);
        goodsname = (TextView) findViewById(R.id.editText57);
        plQysum = (EditText) findViewById(R.id.editText29);

        /*货品名称滚动*/
        roll(goodsname);

        /*回车事件复写*/
        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        if (querypostion()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        });


        postion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querypostion();
                }
            }
        });


        waregood.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if (odd % 2 != 0) {
                        quergood();
                        if (waregood.getError() == null) {
                            plQysum.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    plQysum.requestFocus();
                                }
                            }, 400);
                        }
                    }
                    return true;
                }
                return true;
            }
        });

        waregood.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    quergood();
                }
            }
        });
    }

    /* 查询货位*/
    public Boolean querypostion() {
        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        if (Textpos.equals("")) {
            postion.setError("请输入正确的货位编码！");
            return true;
        }

        Hashtable ParamValue = new Hashtable<>();
//        ParamValue.put("SPName", "PRO_GETPOSITIONID");
        ParamValue.put("SPName", "PRO_UP_GOODS_CHECK_POSITION");
        ParamValue.put("whid", AppStart.GetInstance().Warehouse);
        ParamValue.put("PosFullCode", Textpos);
        ParamValue.put("type", 1);
        ParamValue.put("msg", "output-varchar-100");
        List<Hashtable> result = new ArrayList<Hashtable>();
        result = Datarequest.GETstored(ParamValue);
        if (result.get(0).get("result").toString().equals("0.0")) {
            return false;
        } else {
            postion.setError(result.get(0).get("msg").toString());
            postion.setText(postion.getText().toString());//添加这句后实现效果
            Spannable content = postion.getText();
            Selection.selectAll(content);
            return true;
        }


    }


    public void quergood() {
        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        String Textware = TransactSQL.instance.filterSQL(waregood.getText().toString());
        if (Textpos.equals("")) {
            postion.setError("请输入正确的货位编码");
            return;
        }
        if (Textware.equals("")) {
            waregood.setError("请输入正确的货品编码");
            return;
        }
        List<Hashtable> wareCode = new ArrayList<>();
        String SQL = "select * from v_stock  where posCode='"+Textpos+"' and warehouseId= "+AppStart.GetInstance().Warehouse+"  and wareGoodsCodes='"+Textware+"'";
        //and whAareType!='SH'
        wareCode = Datarequest.GetDataArrayList(SQL);
        if (wareCode.size() != 0) {
            bhsum = Integer.parseInt(wareCode.get(0).get("realStock").toString());
            goodsname.setText(wareCode.get(0).get("goodsName").toString());
        } else {
            selectall(waregood);
            waregood.setError("该货位下没有该货品！");
            return;
        }
    }


    public void backtasks(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(transfer_pick.this, transfer_task.class);
        startActivity(myIntent);
        transfer_pick.this.finish();
    }


    /* 拣货保存*/
    public void savasumbit(View V) {

        if (querypostion()) {
            postion.setError("请输入正确的货位编码！");
            postion.requestFocus();
            return;
        }

        if (goodsname.getText().toString().equals("")) {
            waregood.setError("请输入正确的货品编码！");
            waregood.requestFocus();
            return;
        }

        if (plQysum.getText().toString().equals("")) {
            plQysum.setError("请输入上货数量！");
            waregood.requestFocus();
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        Hashtable ParamValues = new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
                        ParamValues.put("msg", "output-varchar-8000");

                        ParamValues.put("mgotype", "3");
                        ParamValues.put("mgoNo", order);


                        final String wareGoodsCodes = TransactSQL.instance.filterSQL(waregood.getText().toString());
                        ParamValues.put("wareGoodsCodes", wareGoodsCodes);


                        String realStock = TransactSQL.instance.filterSQL(plQysum.getText().toString());
                        ParamValues.put("realStock", realStock);

                        int userId = AppStart.GetInstance().getUserID();
                        ParamValues.put("createId", "" + userId + "");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);


                        String posFullCode = TransactSQL.instance.filterSQL(postion.getText().toString());
                        ParamValues.put("posFullCode", posFullCode);
                        ParamValues.put("inPosFullCode", "");
                        List<Hashtable> result = new ArrayList<Hashtable>();
                        result = Datarequest.GETstored(ParamValues);
                        if (result.get(0).get("result").toString().equals("0.0")) {

                            new AlertDialog.Builder(transfer_pick.this)
                                    .setTitle("提示")
                                    .setMessage("是否继续拣货！")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                            postion.setText("");
                                            goodsname.setText("");
                                            waregood.setText("");
                                            plQysum.setText("");

                                            postion.setFocusable(true);
                                            postion.setFocusableInTouchMode(true);
                                            postion.requestFocus();
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setClass(transfer_pick.this, transfer_task.class);
                                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                                            dialog.dismiss();
                                            transfer_pick.this.finish();
                                        }
                                    })
                                    .show();

                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(transfer_pick.this);
                            build.setMessage(result.get(0).get("msg").toString()).show();
                        }

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


    /*返回键拦截*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(transfer_pick.this, transfer_task.class);
            startActivity(myIntent);
            transfer_pick.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
