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
import android.view.WindowManager;
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
 * Created by gfgh on 2016/4/22.
 */
public class reple_replenishment extends buttom_state {

    private List<Map<String, String>> gridData= new ArrayList<Map<String, String>>();
    private EditText
            inputsku,
            inputpostion,
            inputsum;

    private TextView
            bhorder,
            goodsku,
            jhsum,
            hwcode;

    private int sum=0,datasum=0,odd=0;
    private String nextsku,replenorder,jhpostion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reple_replenishment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        gridData= (List) bundle.getSerializable("griddata");
        replenorder=intent.getStringExtra("order");
        jhpostion=intent.getStringExtra("jhpostion");
        bhorder=(TextView)findViewById(R.id.editText56);
        goodsku=(TextView)findViewById(R.id.editText57);
        jhsum=(TextView)findViewById(R.id.editText58);
        hwcode=(TextView)findViewById(R.id.textView130);

        /*货品名称滚动*/
        roll(goodsku);

        List<Hashtable> gridDatas = new ArrayList<Hashtable>();
        String SQLs = "select * from v_fillgoods where mgoNo ='"+replenorder+"' and wareGoodsCodes='"+gridData.get(0).get("wareGoodsCodes").toString()+"'";
        gridDatas = Datarequest.GetDataArrayList(SQLs);
        String surplus="";
        if (gridDatas.size()!= 0) {
            surplus=gridDatas.get(0).get("stock").toString();
        }else {

            surplus=gridData.get(0).get("planQty").toString();
        }


        bhorder.setText(replenorder);
        goodsku.setText(gridData.get(0).get("wareGoodsCodes").toString());
        jhsum.setText(surplus);
        hwcode.setText(jhpostion);



        inputpostion=(EditText)findViewById(R.id.editText45);
        inputsku=(EditText)findViewById(R.id.editText46);
        inputsum=(EditText)findViewById(R.id.editText29);



        inputpostion.setFocusable(true);
        inputpostion.setFocusableInTouchMode(true);
        inputpostion.requestFocus();

        /*回车事件复写*/
        inputsku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {

                        if (!inputsku.getText().toString().equals(goodsku.getText().toString())) {
                            inputsku.setError("货品条码不匹配！");
                            inputsku.setText(inputsku.getText().toString());//添加这句后实现效果
                            Spannable content = inputsku.getText();
                            Selection.selectAll(content);
                            return true;
                        }else {
                            inputsku.setText(inputsku.getText().toString());//添加这句后实现效果
                            Spannable content = inputsku.getText();
                            Selection.selectAll(content);
                            replesum();
                        }

                    }
                    return true;
                }
                return true;
            }
        });


        /*失去焦点触发事件*/
        inputsku.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!inputsku.getText().toString().equals(goodsku.getText().toString())) {
                        inputsku.setError("货品条码不匹配！");
                        inputsku.setText(inputsku.getText().toString());//添加这句后实现效果
                        Spannable content = inputsku.getText();
                        Selection.selectAll(content);
                    }else {
                        inputsku.setText(inputsku.getText().toString());//添加这句后实现效果
                        Spannable content = inputsku.getText();
                        Selection.selectAll(content);
                        replesum();
                    }
                }
            }
        });




        /*回车事件复写*/
        inputpostion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if(odd%2!=0){

                        String inputpos=inputpostion.getText().toString();
                        if(!hwcode.getText().toString().equals(inputpos)){
                            inputpostion.setError("货位不匹配！");
                            inputpostion.setText(inputpostion.getText().toString());
                            Spannable content = inputpostion.getText();
                            Selection.selectAll(content);
                            return  true;
                        }
                        return  false;
                    }

                    return true;
                }
                return false;
            }
        });


        /*失去焦点触发事件*/
        inputpostion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String inputpos=inputpostion.getText().toString();
                    if(!hwcode.getText().toString().equals(inputpos)){
                        inputpostion.setError("货位不匹配！");
                        inputpostion.setText(inputpostion.getText().toString());
                        Spannable content = inputpostion.getText();
                        Selection.selectAll(content);

                    }
                }
            }
        });




        EditText editText29=(EditText)findViewById(R.id.editText29);
        //输入框值change事件
        editText29.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {

                        try{
                            int lst=Integer.parseInt(jhsum.getText().toString());
                            int sums=Integer.parseInt(inputsum.getText().toString());
                            if(sums>lst){
                                EditText editText29=(EditText)findViewById(R.id.editText29);
                                editText29.setError("不能大于拣货数量！");
                                editText29.setText("");
                            }
                        }catch (Exception e){
                            EditText editText29=(EditText)findViewById(R.id.editText29);
                            editText29.setError("补货数量不正确!");
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




    public  void   replesum(){

        datasum++;
        int lst=Integer.parseInt(jhsum.getText().toString());
        if(datasum<=lst){
            inputsum.setText(""+datasum+"");
        }else {
            AlertDialog.Builder build = new AlertDialog.Builder(reple_replenishment.this);
            build.setMessage("不能大于拣货数量！").show();
            datasum=Integer.parseInt(jhsum.getText().toString());
        }

    }

    public  void repleclick(View v){

        String jhtext = TransactSQL.instance.filterSQL(hwcode.getText().toString());
        if(!jhtext.equals(inputpostion.getText().toString())) {
            inputpostion.setError("请输入正确的货位！");
            return;
        }


        String skutext = TransactSQL.instance.filterSQL(inputsku.getText().toString());
        if(!skutext.equals(goodsku.getText().toString())){
            inputsku.setError("货品条码不匹配！");
            return;
        }

        int inputsums=0;
        try{
             inputsums=Integer.parseInt(inputsum.getText().toString());
        }catch (Exception e){

        }

        int lst=Integer.parseInt(jhsum.getText().toString());
        if(inputsums>lst){
            AlertDialog.Builder build = new AlertDialog.Builder(reple_replenishment.this);
            build.setMessage("补货数量不正确！").show();
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        Hashtable ParamValues =new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_FILL");
                        ParamValues.put("msg", "output-varchar-8000");

                        ParamValues.put("mgotype","2");
                        ParamValues.put("mgoNo",replenorder);

                        EditText text22=(EditText)findViewById(R.id.editText46);
                        String wareGoodsCodes = TransactSQL.instance.filterSQL(text22.getText().toString());
                        ParamValues.put("wareGoodsCodes",wareGoodsCodes);
                        EditText text29=(EditText)findViewById(R.id.editText29);
                        ParamValues.put("realStock",text29.getText().toString());

                        int userId=AppStart.GetInstance().getUserID();
                        ParamValues.put("createId",""+userId+"");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);
                        EditText text21=(EditText)findViewById(R.id.editText45);
                        String inPosFullCode = TransactSQL.instance.filterSQL(text21.getText().toString());
                        ParamValues.put("inPosFullCode", inPosFullCode);



                        List<Hashtable> data = new ArrayList<Hashtable>();
                        data = Datarequest.GETstored(ParamValues);
                        if(data.get(0).get("result").toString().equals("0.0")){
                            AlertDialog.Builder build = new AlertDialog.Builder(reple_replenishment.this);
                            build.setMessage("补货成功！").show();

                            Intent intent = new Intent();
                            intent.setClass(reple_replenishment.this, reple_task.class);
                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                            reple_replenishment.this.finish();
                        }else {
                            AlertDialog.Builder build = new AlertDialog.Builder(reple_replenishment.this);
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



    public  void backstask(View v){
        Intent intent = new Intent();
        intent.setClass(reple_replenishment.this, reple_task.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        reple_replenishment.this.finish();

    }
    public  void backsmain(View v){
        Intent intent = new Intent();
        intent.setClass(reple_replenishment.this, library_management.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        reple_replenishment.this.finish();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(reple_replenishment.this, reple_task.class);
            startActivity(myIntent);
            reple_replenishment.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







}














