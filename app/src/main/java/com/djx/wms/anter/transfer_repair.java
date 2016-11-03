package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
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
public class transfer_repair extends buttom_state {

    private String order="",nextgoods="";
    private TextView orderinput,goodsname,surplussum;
    private  int sum=0,bhsum=0,odd=0;
    private EditText postion,waregood,plQysum;
    private  Boolean statce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_repair);

        Intent intent=getIntent();
        order= intent.getStringExtra("order");
        orderinput=(TextView)findViewById(R.id.editText56);
        orderinput.setText(order);


        postion=(EditText)findViewById(R.id.editText45);
        waregood=(EditText)findViewById(R.id.editText46);
        plQysum=(EditText)findViewById(R.id.editText29);

        goodsname=(TextView)findViewById(R.id.editText57);
        surplussum=(TextView)findViewById(R.id.textView129);

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
                        }else{
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
                    }
                    return true;
                }
                return true;
            }
        });


        waregood.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    quergood();
                }
            }
        });








        //输入框值change事件
        plQysum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText editText29 = (EditText) findViewById(R.id.editText29);
                        if (!editText29.getText().toString().equals("")) {
                            int jhsum=0;
                            try{jhsum = Integer.parseInt(editText29.getText().toString());
                            }catch (Exception e){}

                            if (jhsum > bhsum || jhsum <=0) {
                                editText29.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(transfer_repair.this);
                                build.setMessage("上货数量不正确！").show();
                                /*pos = false;*/
                            }

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


     public void backtask(View v){
         Intent myIntent = new Intent();
         myIntent = new Intent(transfer_repair.this, transfer_task.class);
         startActivity(myIntent);
         transfer_repair.this.finish();
     }


    /* 查询货位*/
    public Boolean querypostion(){


        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        if(Textpos.equals("")){
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




    public void quergood(){
        String Textware = TransactSQL.instance.filterSQL(waregood.getText().toString());
        if(Textware.equals("")){
          /*  waregood.setError("请输入正确的货品条码！");*/
            return;
        }

        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        if(Textpos.equals("")){
            return ;
        }
        waregood.setText(waregood.getText().toString());//添加这句后实现效果
        Spannable content = waregood.getText();
        Selection.selectAll(content);
        if(Textware.equals(nextgoods)){
            if(statce){
                int sum=0;
                try{sum=Integer.parseInt(plQysum.getText().toString());}catch (Exception e){}
                sum++;
                plQysum.setText(""+sum+"");
            }else {
                waregood.setError("移出货位下没有该货品库存！");
            }


        }else{

            List<Hashtable>  wareCode = new ArrayList<Hashtable>();
            String SQL = "select * from v_fillgoods  where mgoNo='"+order+"' and whId='"+AppStart.GetInstance().Warehouse+"' and wareGoodsCodes='"+Textware+"' ";
            wareCode = Datarequest.GetDataArrayList(SQL);
            if(wareCode.size()!=0){
                bhsum= Integer.parseInt(wareCode.get(0).get("stock").toString());
                plQysum.setText("1");
                goodsname.setText(wareCode.get(0).get("goodsName").toString());
                surplussum.setText(wareCode.get(0).get("stock").toString());
                waregood.setError(null);
                statce=true;
            }else {
                statce=false;
                waregood.setError("移出货位下没有该货品库存！");
            }


        }

        nextgoods=Textware;
    }




    public  void savasumbits(View v){

        if(querypostion()){
            postion.setError("请输入正确的货位编码！");
            return;
        }

        if(goodsname.getText().toString().equals("")){
            waregood.setError("请输入正确的货品编码！");
            return;
        }

        if(plQysum.getText().toString().equals("")){
            plQysum.setError("请输入上货数量！");
            return;
        }





        new AlertDialog.Builder(transfer_repair.this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")

                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        Hashtable ParamValues =new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_FILL");
                        ParamValues.put("msg", "output-varchar-8000");
                        ParamValues.put("mgotype","3");
                        ParamValues.put("mgoNo",order);
                        String wareGoodsCodes = TransactSQL.instance.filterSQL(waregood.getText().toString());
                        ParamValues.put("wareGoodsCodes",wareGoodsCodes);
                        ParamValues.put("realStock", plQysum.getText().toString());
                        int userId=AppStart.GetInstance().getUserID();
                        ParamValues.put("createId",""+userId+"");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);
                        String inPosFullCode = TransactSQL.instance.filterSQL(postion.getText().toString());
                        ParamValues.put("inPosFullCode", inPosFullCode);
                        List<Hashtable> data = new ArrayList<Hashtable>();
                        data = Datarequest.GETstored(ParamValues);


                        if(data.get(0).get("result").toString().equals("0.0")){
                            new AlertDialog.Builder(transfer_repair.this)
                                    .setTitle("提示")
                                    .setMessage("是否继续上货！")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub


                                            /*清空页面*/
                                            goodsname.setText("");
                                            surplussum.setText("");
                                            postion.setText("");
                                            waregood.setText("");
                                            plQysum.setText("");
                                            nextgoods="";

                                            postion.setFocusable(true);
                                            postion.setFocusableInTouchMode(true);
                                            postion.requestFocus();
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setClass(transfer_repair.this, transfer_task.class);
                                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                                            transfer_repair.this.finish();
                                        }
                                    })
                                    .show();

                        }else {
                            AlertDialog.Builder build = new AlertDialog.Builder(transfer_repair.this);
                            build.setMessage(data.get(0).get("msg").toString()).show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(transfer_repair.this, transfer_task.class);
            startActivity(myIntent);
            transfer_repair.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
