package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
 * Created by gfgh on 2016/8/16.
 */
public class temporary_lnventory extends buttom_state {

    private EditText postion, waregood, sum;
    private List<Hashtable> listhas = new ArrayList<Hashtable>();
    private int odd = 0;
    private String XIAOTAG = "XiaoTest";//log日志打印标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary_lnventory);


        postion = (EditText) findViewById(R.id.editText45);
        waregood = (EditText) findViewById(R.id.editText46);
        sum = (EditText) findViewById(R.id.editText29);

       /*回车事件复写*/
        waregood.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if (odd % 2 != 0) {
                        if (query_waregood()) {
                            return false;
                        } else {
                            selectall(waregood);
                            return true;
                        }
                    }

                }
                return true;
            }
        });

        waregood.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {/*判断是否有焦点*/
                if (!hasFocus)
                    query_waregood();
            }
        });


        //输入框值change事件
        sum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {

                        int sumConttext = 0;
                        try {
                            sumConttext = Integer.parseInt(sum.getText().toString());
                        } catch (Exception e) {
                        }
                        if (sumConttext == 0) {
                            sum.setError("数量不正确！");
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });


    }

    private Boolean query_waregood() {
        String wareContext = TransactSQL.instance.filterSQL(waregood.getText().toString());
        String SQL = "select * from   v_querygoods_android  where  wareGoodsCodes='" + wareContext + "' ";
        listhas = Datarequest.GetDataArrayList(SQL);

        if (listhas.size() != 0) {
            return true;
        } else {
            waregood.setError("货品信息不正确！");
            return false;
        }
    }

    public void sumbitTemporary_lnventory(View v) {

        String wareContext = TransactSQL.instance.filterSQL(waregood.getText().toString());
        if (wareContext == null || query_waregood() != true) {
            waregood.setError("货品信息不正确!");
            return;
        }

        try {
            Integer.parseInt(sum.getText().toString());
        } catch (Exception e) {
            sum.setError("数量信息不正确!");
            return;
        }

        if (Integer.parseInt(sum.getText().toString()) == 0) {
            sum.setError("数量信息不正确!");
            return;
        }


        String postionContext = TransactSQL.instance.filterSQL(postion.getText().toString());
        Hashtable ParamValue = new Hashtable<>();
        ParamValue.put("SPName", "sp_inventory_temp");
        ParamValue.put("posCode", postionContext);
        ParamValue.put("barCode", wareContext);
        ParamValue.put("realQty", sum.getText().toString());
        ParamValue.put("userId", AppStart.GetInstance().getUserID());

        ParamValue.put("userId", AppStart.GetInstance().getUserID());
        ParamValue.put("whId", AppStart.GetInstance().Warehouse);

        ParamValue.put("msg", "output-varchar-8000");

        List<Hashtable> result = new ArrayList<Hashtable>();
        result = Datarequest.GETstored(ParamValue);
        if (result.get(0).get("result").toString().equals("0.0")) {
            AlertDialog.Builder build = new AlertDialog.Builder(temporary_lnventory.this);
            build.setMessage("盘点成功！").show();


            Intent myIntent = new Intent();
            myIntent = new Intent(temporary_lnventory.this, temporary_lnventory.class);
            startActivity(myIntent);
            temporary_lnventory.this.finish();


        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(temporary_lnventory.this);
            build.setMessage(result.get(0).get("msg").toString()).show();
        }


    }


    public void backtwolevel_menu(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(temporary_lnventory.this, library_management.class);
        startActivity(myIntent);
        temporary_lnventory.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(temporary_lnventory.this, library_management.class);
            startActivity(myIntent);
            temporary_lnventory.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
