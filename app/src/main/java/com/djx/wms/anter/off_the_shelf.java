package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
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
 * Created by gfgh on 2016/6/14.
 */
public class off_the_shelf extends buttom_state {

    private EditText postionCode;
    private TextView tv_explain;
    private int odds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.off_the_shelf);

        tv_explain = (TextView) findViewById(R.id.textView147);
        String explain = "下架功能使用场景例子：<br />"+
                "1、拣货区一货一位时，发现货位上有商品，但是需要另外的商品放入该货位，就需要将原有商品下架，如果货品库存不为0，则需要执行<font color = '#FF0000'>移库</font>操作将库存调整为0。<br />" +
                "2、备货区一位多货时，如果需要更换货位上的某些商品，下架功能就会下架该货位下<font color = '#FF0000'>库存为0的所有商品</font>，如果库存不为0，则需要执行<font color = '#FF0000'>移库</font>操作将库存调整为0。<br />";
        tv_explain.setText(Html.fromHtml(explain));
        tv_explain.setTextSize(18);

        postionCode = (EditText) findViewById(R.id.editText7);
        postionCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odds++;
                    if (odds % 2 != 0) {
                        off_shlef(v);
                        selectall(postionCode);
                    }
                    return true;
                }
                return true;
            }
        });


    }


    public void off_shlef(View v) {


        String jhPosition = postionCode.getText().toString();
        /* SQL过滤*/
        jhPosition = TransactSQL.instance.filterSQL(jhPosition);
        if (jhPosition.equals("")) {
            postionCode.setError("请输入正确的货位编码！");
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否下架？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String jhPosition = postionCode.getText().toString();
                                jhPosition = TransactSQL.instance.filterSQL(jhPosition);

                                Hashtable ParamValue = new Hashtable<>();
                                ParamValue.put("SPName", "PRO_NEXT_GOODS");
                                ParamValue.put("whid", AppStart.GetInstance().Warehouse);
                                int userId = AppStart.GetInstance().getUserID();
                                ParamValue.put("userID", "" + userId + "");
                                ParamValue.put("PosFullCode", jhPosition);
                                ParamValue.put("msg", "output-varchar-8000");
                                List<Hashtable> result = new ArrayList<Hashtable>();
                                result = Datarequest.GETstored(ParamValue);
                                if (result.get(0).get("result").toString().equals("0.0")) {
                                    AlertDialog.Builder build = new AlertDialog.Builder(off_the_shelf.this);
                                    build.setMessage("下架成功！").show();
                                } else {
                                    AlertDialog.Builder build = new AlertDialog.Builder(off_the_shelf.this);
                                    build.setMessage(result.get(0).get("msg").toString()).show();
                                }


                            }
                        }

                )
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                )
                .show();


    }


    /*返回*/
    public void offshlf_back(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(off_the_shelf.this, library_management.class);
        startActivity(myIntent);
        off_the_shelf.this.finish();

    }


    /*返回复写*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(off_the_shelf.this, library_management.class);
            startActivity(myIntent);
            off_the_shelf.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
