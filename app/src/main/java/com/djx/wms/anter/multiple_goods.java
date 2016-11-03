package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Map;

/**
 * Created by gfgh on 2016/5/20.
 */
public class multiple_goods extends buttom_state {

    private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();
    private int  odd=0;
    private TextView textCode,goodsname,textpostion;
    private EditText postion,waregood,plQysum;
    private  Boolean statce=false;
    private String nextgoods="";

    private int TIME = 20;
    private  Runnable runnables;
    private Handler handlers = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_goods);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        listData= (List) bundle.getSerializable("data");

        textCode=(TextView)findViewById(R.id.textView130);
        goodsname=(TextView)findViewById(R.id.editText57);
        textpostion=(TextView)findViewById(R.id.editText58);

        postion=(EditText)findViewById(R.id.editText45);
        waregood=(EditText)findViewById(R.id.editText46);
        plQysum=(EditText)findViewById(R.id.editText29);
        /*货品滚动*/
        roll(goodsname);

        textCode.setText(listData.get(0).get("wareGoodsCodes").toString());
        goodsname.setText(listData.get(0).get("goodsName").toString());
        textpostion.setText(listData.get(0).get("posCode").toString());

       /* warecode.setText(warecode.getText().toString());//添加这句后实现效果
        Spannable content = warecode.getText();
        Selection.selectAll(content);*/



        runnables=new Runnable() {
            @Override
            public void run() {
                // handler自带方法实现定时器
                try {
                    handlers.postDelayed(this, TIME);
                    postion.setFocusable(true);
                    postion.setFocusableInTouchMode(true);
                    postion.requestFocus();
                    System.out.println("测试");
                    handlers.removeCallbacks(runnables);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        handlers.postDelayed(runnables, TIME);



        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (querypostion()) {
                        return true;
                    } else {
                        return false;
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

    }





    /* 查询货位*/
    public Boolean querypostion(){
        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        String textpostions=TransactSQL.instance.filterSQL(textpostion.getText().toString());
        if(Textpos.equals("")){
            return true;
        }

        if (textpostions.equals(Textpos)) {
            return false;
        } else {
            postion.setText(postion.getText().toString());//添加这句后实现效果
            Spannable content = postion.getText();
            Selection.selectAll(content);
            postion.setError("货位编码不匹配！");
            return true;
        }

    }



    public void quergood(){
        String Textpos = TransactSQL.instance.filterSQL(postion.getText().toString());
        String Textware = TransactSQL.instance.filterSQL(waregood.getText().toString());
        if(Textware.equals("")){
            return;
        }

        if(Textpos.equals("")){
            return;
        }

        waregood.setText(waregood.getText().toString());//添加这句后实现效果
        Spannable content = waregood.getText();
        Selection.selectAll(content);
        if(Textware.equals(nextgoods)){
            if(statce){
                int sum=0;
                sum=Integer.parseInt(plQysum.getText().toString());
                sum++;
                plQysum.setText(""+sum+"");
            }else {
                waregood.setError("货品条码不匹配！");
                plQysum.setText("");
            }

        }else {

            if(Textware.equals(textCode.getText().toString())){
                plQysum.setText("1");
                statce=true;
            }else {
                statce=false;
                waregood.setError("货品不匹配！");
                plQysum.setText("");
            }
        }
        nextgoods=Textware;


    }


    public void  backmany_goods(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(multiple_goods.this, many_goods.class);
        startActivity(myIntent);
        multiple_goods.this.finish();
    }


    /*上货保存*/
    public  void multiple_sumbit(View V){

        if(querypostion()){
            postion.setError("请输入正确的货位编码！");
            return;
        }

        if(waregood.getText().toString().equals("")){
            waregood.setError("请输入正确的货品编码！");
            return;
        }

        if(plQysum.getText().toString().equals("")){
            plQysum.setError("请输入上货数量！");
            return;
        }




        new AlertDialog.Builder(multiple_goods.this)
                .setTitle("提示")
                .setMessage("你将完成该订单！")

                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        Hashtable ParamValues =new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
                        ParamValues.put("msg", "output-varchar-8000");

                        ParamValues.put("mgotype", "4");
                        ParamValues.put("mgoNo", "");


                        String   wareGoodsCodes=TransactSQL.instance.filterSQL(waregood.getText().toString());
                        ParamValues.put("wareGoodsCodes",wareGoodsCodes);


                        String   realStock=TransactSQL.instance.filterSQL(plQysum.getText().toString());
                        ParamValues.put("realStock",realStock);

                        int  userId=AppStart.GetInstance().getUserID();
                        ParamValues.put("createId",""+userId+"");
                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);


                        String   posFullCode=TransactSQL.instance.filterSQL(postion.getText().toString());
                        ParamValues.put("posFullCode","");
                        ParamValues.put("inPosFullCode",posFullCode);
                        List<Hashtable> result = new ArrayList<Hashtable>();
                        result = Datarequest.GETstored(ParamValues);
                        if(result.get(0).get("result").toString().equals("0.0")){
                            AlertDialog.Builder build = new AlertDialog.Builder(multiple_goods.this);
                            build.setMessage("上货成功！").show();

                            Intent intent = new Intent();
                            intent.setClass(multiple_goods.this, many_goods.class);
                            startActivity(intent);
                            multiple_goods.this.finish();
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(multiple_goods.this);
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








    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(multiple_goods.this, many_goods.class);
            startActivity(myIntent);
            multiple_goods.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
