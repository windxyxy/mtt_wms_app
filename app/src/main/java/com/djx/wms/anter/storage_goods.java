package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/5/10.
 */
public class storage_goods extends buttom_state {


    private TextView order,
            goodname,
            merchantcode;

    private EditText goodbarcode,
            goodsum;


    private List<Map<String, String>> listhas= new ArrayList<Map<String, String>>();

    private String coderecording="";
    private int sum=0;
    private  String storageorder="";
    private int sums=0;
    private int TIME = 10;
    private  Runnable runnables;
    private Handler handlers = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_goods);

        goodbarcode=(EditText)findViewById(R.id.editText32);
        goodsum=(EditText)findViewById(R.id.editText28);
        order=(TextView)findViewById(R.id.editText30);
        goodname=(TextView)findViewById(R.id.editText26);
        merchantcode=(TextView)findViewById(R.id.textView122);

        /*货品名称滚动*/
        roll(goodname);
        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        storageorder=intent.getStringExtra("storageorder");
        listhas = (List) bundle.getSerializable("datas");
        order.setText(storageorder);


        goodbarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums % 2 != 0) {
                        querygood(v);
                    }
                    return true;
                }
                return false;
            }
        });





            runnables = new Runnable() {

                @Override
                public void run() {
                    // handler自带方法实现定时器
                    try {
                        handlers.postDelayed(this, TIME);
                        goodbarcode.setFocusable(true);
                        goodbarcode.setFocusableInTouchMode(true);
                        goodbarcode.requestFocus();
                        System.out.println("测试");
                        handlers.removeCallbacks(runnables);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            handlers.postDelayed(runnables ,TIME);


                  /*光标下移触发事件*/
        goodbarcode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querygood(v);
                }
            }
        });


    }




    public void querygood(View v){
        goodbarcode.setText(goodbarcode.getText().toString());//添加这句后实现效果
        Spannable content = goodbarcode.getText();
        Selection.selectAll(content);

        String bartext=goodbarcode.getText().toString();
        bartext= TransactSQL.instance.filterSQL(bartext);

        /*条码不能为空*/
        if(bartext.equals("")){
            goodbarcode.setError("请输入正确的条码！");
            return;
        }

        /* 重复数量递增*/
        if(coderecording.equals(bartext)){
         sum++;
         goodsum.setText(""+sum+"");
         return;
        }

        coderecording=bartext;

        /*判断是否存在*/
        for(Map i:listhas){
            String goodbarcode=i.get("wareGoodsCodes").toString();
            if(goodbarcode.equals(bartext)){
                goodname.setText(i.get("goodsName").toString());
                merchantcode.setText(i.get("goodsCode").toString());
                sum=1;
                goodsum.setText(""+sum+"");
                return;
            }
        }


        /*没有操作*/
        goodsum.setText("");
        goodname.setText("");
        merchantcode.setText("");
        goodbarcode.setError("没有对应商品！");
        coderecording="";
    }



    public void  storageback(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(storage_goods.this,storage_enquiry.class);
        startActivity(myIntent);
        storage_goods.this.finish();
    }


    public void sumbit(View v){

        String bartext=goodbarcode.getText().toString();
        bartext= TransactSQL.instance.filterSQL(bartext);

        if(bartext.equals("")){
            goodbarcode.setError("请输入正确的条码！");
            return;
        }


        if(goodname.getText().toString().equals("")){
            AlertDialog.Builder build = new AlertDialog.Builder(storage_goods.this);
            build.setMessage("请扫入条码！").show();
            return;
        }


        if(goodsum.getText().toString().equals("0") || goodsum.getText().toString().equals("")){
            goodsum.setError("数量不能为0或者为空！");
            goodsum.setText("");
            return;
        }



        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Hashtable Param= new Hashtable<>();
                        Param.put("SPName", "PRO_INSTOCK_HANDHELD_ADD");
                        /*条码*/
                        String bartext=goodbarcode.getText().toString();
                        bartext= TransactSQL.instance.filterSQL(bartext);
                        /*用户ID*/
                        int   userid=AppStart.GetInstance().getUserID();
                        Param.put("inStockNo", storageorder);
                        Param.put("wareGoodsCodes",bartext);
                        Param.put("realStock",goodsum.getText().toString());
                        Param.put("createId",""+userid+"");
                        Param.put("msg", "output-varchar-8000");

                        List<Hashtable> sumbitsava= new ArrayList<Hashtable>();
                        sumbitsava= Datarequest.GETstored(Param);
                        if(sumbitsava.get(0).get("result").toString().equals("0.0")){
                            AlertDialog.Builder build = new AlertDialog.Builder(storage_goods.this);
                            build.setMessage("入库成功！").show();
                        }else {
                            AlertDialog.Builder build = new AlertDialog.Builder(storage_goods.this);
                            build.setMessage(sumbitsava.get(0).get("msg").toString()).show();
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(storage_goods.this, storage_enquiry.class);
            startActivity(myIntent);
            storage_goods.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    /* 页面加载完成*//*
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        goodbarcode.setFocusable(true);
        goodbarcode.setFocusableInTouchMode(true);
        goodbarcode.requestFocus();
    }*/
}
