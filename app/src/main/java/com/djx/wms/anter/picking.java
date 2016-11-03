package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by gfgh on 2016/4/19.
 */
public class picking extends buttom_state {

    private EditText
            inputpostiong,
            inputsku,
            jhsum;

    private TextView textCode,
            textpostion,
            textTname,
            textsum,
            currentsum,
            totalsum,
            nextpostion;

    private String nextgood="",jhorder="", datalsit="";;
    private List<Hashtable> listhas = new ArrayList<Hashtable>();
    private List<Map<String, String>> gridData= new ArrayList<Map<String, String>>();

    private  int nextpage=0,sum=0,odd=0,foussum=0;
    private String[] str;
    private Boolean  staus=false;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.picking);


        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        jhorder=intent.getStringExtra("order");
        gridData= (List) bundle.getSerializable("listdatas");

        textpostion=(TextView)findViewById(R.id.editText13);
        textCode=(TextView)findViewById(R.id.editText32);
        textTname=(TextView)findViewById(R.id.editText33);
        textsum=(TextView)findViewById(R.id.editText34);
        nextpostion=(TextView)findViewById(R.id.textView133);

        /*货品名称滚动*/
        roll(textTname);
        currentsum=(TextView)findViewById(R.id.textView125);
        totalsum=(TextView)findViewById(R.id.textView127);




        currentsum.setText("1");
        int length=gridData.size();
        totalsum.setText("" + length + "");





        textpostion.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
        textCode.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
        textTname.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsName").toString());
        textsum.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsNum").toString());
        try{
            nextpostion.setText(gridData.get(AppStart.GetInstance().nextsum+1).get("goodsPos").toString());
        }catch (Exception e){}


      /* textpostion.setText(gridData.get(0).get("goodsPos").toString());
        textCode.setText(gridData.get(0).get("wareGoodsCodes").toString());
        textTname.setText(gridData.get(0).get("goodsName").toString());
        textsum.setText(gridData.get(0).get("goodsNum").toString());

        try{
            nextpostion.setText(gridData.get(1).get("goodsPos").toString());
        }catch (Exception e){}*/



        inputpostiong=(EditText)findViewById(R.id.editText35);
        inputsku=(EditText)findViewById(R.id.editText36);
        jhsum=(EditText)findViewById(R.id.editText37);

        startdata();

        //输入框值change事件
        jhsum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!jhsum.getText().toString().equals("")) {
                            int jsum=0;
                            try {
                                jsum = Integer.parseInt(jhsum.getText().toString());
                            }catch (Exception e){};

                            int djsum = Integer.parseInt(textsum.getText().toString());
                            if (jsum > djsum || jsum <= 0) {
                                jhsum.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
                                build.setMessage("拣货数量不正确！").show();
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





        inputpostiong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if(odd%2!=0) {
                        if (querjhpos()) {
                            return true;
                        } else {
                            inputsku.setFocusable(true);
                            inputsku.setFocusableInTouchMode(true);
                            inputsku.requestFocus();
                            inputsku.requestFocusFromTouch();
                            return false;
                        }
                    }
                }
                return true;
            }
        });





        inputsku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if(sum%2!=0){
                        quergood();
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });






        /*失去焦点触发事件*/
        inputsku.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!staus){
                      quergood();
                    }

                }else {
                    /*解决焦点下移问题*/
                   /* if(foussum==0){
                        foussum++;
                        inputpostiong.setFocusable(true);
                        inputpostiong.setFocusableInTouchMode(true);
                        inputpostiong.requestFocus();
                    }*/
                }


            }
        });


         /*失去焦点触发事件*/
        inputpostiong.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querjhpos();
                }
            }
        });





    }





    /*下一页*/
    public void nextpages(View v){
        String textpostiongs=inputpostiong.getText().toString();
        String textsku=inputsku.getText().toString();
        String sum=jhsum.getText().toString();
        textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
        textsku = TransactSQL.instance.filterSQL(textsku);
        sum = TransactSQL.instance.filterSQL(sum);
        if(textpostiongs.equals("")&&textsku.equals("")&&sum.equals("")){
            nextdata();
            return;
        }

        if(!textpostiongs.equals(textpostion.getText().toString())||!textsku.equals(textCode.getText().toString()) ||!sum.equals(textsum.getText().toString()))
        {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("信息不匹配是否继续？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            nextdata();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else {
            nextdata();
        }






    }



    /* 上一页数据加载*/
    public void nextdata(){
        staus=true;
        AppStart.GetInstance().nextsum++;
        if(AppStart.GetInstance().nextsum<gridData.size()&& AppStart.GetInstance().nextsum>=0){

            nextgood="";
            String textpostiongs=inputpostiong.getText().toString();
            String textsku=inputsku.getText().toString();
            String sum=jhsum.getText().toString();

            textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
            textsku = TransactSQL.instance.filterSQL(textsku);
            sum = TransactSQL.instance.filterSQL(sum);

            textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
            textsku = TransactSQL.instance.filterSQL(textsku);
            sum = TransactSQL.instance.filterSQL(sum);

            if(!textpostiongs.equals("")&&!textsku.equals("")&&!sum.equals("")){
                if(textpostiongs.equals(textpostion.getText().toString())&&textsku.equals(textCode.getText().toString()))
                {
                    AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum-1]=""+gridData.get(AppStart.GetInstance().nextsum-1).get("IndexID").toString()+"^"+sum+"";
                }
            }



            textpostion.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
            textCode.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
            textTname.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsName").toString());
            textsum.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsNum").toString());
            currentsum.setText(""+(AppStart.GetInstance().nextsum+1)+"");
            try{
                nextpostion.setText(gridData.get((AppStart.GetInstance().nextsum+1)).get("goodsPos").toString());
            }catch (Exception e){
                nextpostion.setText("");
            }




            try {
                String[] datssum=AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum].split("\\^");
                if(datssum[0].equals(gridData.get(AppStart.GetInstance().nextsum).get("IndexID").toString())){
                    inputpostiong.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
                    inputsku.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
                    jhsum.setText(datssum[1]);
                }
            }catch (Exception e){
                inputpostiong.setText("");
                inputsku.setText("");
                jhsum.setText("");
            }


            inputpostiong.setFocusable(true);
            inputpostiong.setFocusableInTouchMode(true);
            inputpostiong.requestFocus();
            staus=false;
            inputsku.setError(null);

        }else{
            AppStart.GetInstance().nextsum=gridData.size();
            currentsum.setText(""+AppStart.GetInstance().nextsum+"");
            AppStart.GetInstance().nextsum=AppStart.GetInstance().nextsum-1;
            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
            build.setMessage("已经是最后一个").show();
        }


    }


   /* 初始化数据加载*/
   public void startdata(){
       staus=true;
       /*  AppStart.GetInstance().nextsum++;*/
       if(AppStart.GetInstance().nextsum<gridData.size()&& AppStart.GetInstance().nextsum>=0){

           nextgood="";
           String textpostiongs=inputpostiong.getText().toString();
           String textsku=inputsku.getText().toString();
           String sum=jhsum.getText().toString();

           textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
           textsku = TransactSQL.instance.filterSQL(textsku);
           sum = TransactSQL.instance.filterSQL(sum);

           textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
           textsku = TransactSQL.instance.filterSQL(textsku);
           sum = TransactSQL.instance.filterSQL(sum);

           if(!textpostiongs.equals("")&&!textsku.equals("")&&!sum.equals("")){
               if(textpostiongs.equals(textpostion.getText().toString())&&textsku.equals(textCode.getText().toString()))
               {
                   AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum-1]=""+gridData.get(AppStart.GetInstance().nextsum-1).get("IndexID").toString()+"^"+sum+"";
               }
           }



           textpostion.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
           textCode.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
           textTname.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsName").toString());
           textsum.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsNum").toString());
           currentsum.setText(""+(AppStart.GetInstance().nextsum+1)+"");
           try{
               nextpostion.setText(gridData.get((AppStart.GetInstance().nextsum+1)).get("goodsPos").toString());
           }catch (Exception e){
               nextpostion.setText("");
           }




           try {
               String[] datssum=AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum].split("\\^");
               if(datssum[0].equals(gridData.get(AppStart.GetInstance().nextsum).get("IndexID").toString())){
                   inputpostiong.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
                   inputsku.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
                   jhsum.setText(datssum[1]);
               }
           }catch (Exception e){
               inputpostiong.setText("");
               inputsku.setText("");
               jhsum.setText("");
           }




           inputpostiong.setFocusable(true);
           inputpostiong.setFocusableInTouchMode(true);
           inputpostiong.requestFocus();
           staus=false;
           inputsku.setError(null);

       }else{
           AppStart.GetInstance().nextsum=gridData.size();
           currentsum.setText(""+AppStart.GetInstance().nextsum+"");
           AppStart.GetInstance().nextsum=AppStart.GetInstance().nextsum-1;
           AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
           build.setMessage("已经是最后一个").show();
       }


   }




    /* 下一页数据加载*/
    public  void predata(){
        String textpostiongs=inputpostiong.getText().toString();
        String textsku=inputsku.getText().toString();
        String sum=jhsum.getText().toString();

        textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
        textsku = TransactSQL.instance.filterSQL(textsku);
        sum = TransactSQL.instance.filterSQL(sum);

        staus=true;
        AppStart.GetInstance().nextsum--;

        String[] op=AppStart.GetInstance().pickstr;
        int p=AppStart.GetInstance().nextsum;

        if(AppStart.GetInstance().nextsum<gridData.size()&&AppStart.GetInstance().nextsum>=0){

            nextgood="";

            /*输入框值验证*/
            if(!textpostiongs.equals("")&&!textsku.equals("")&&!sum.equals("")){
                if(textpostiongs.equals(textpostion.getText().toString())&&textsku.equals(textCode.getText().toString()))
                {
                    AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum+1]=""+gridData.get(AppStart.GetInstance().nextsum+1).get("IndexID").toString()+"^"+sum+"";
                }
            }


            textpostion.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
            textCode.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
            textTname.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsName").toString());
            textsum.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsNum").toString());
            currentsum.setText(""+(AppStart.GetInstance().nextsum+1)+"");
            try{
                nextpostion.setText(gridData.get((AppStart.GetInstance().nextsum+1)).get("goodsPos").toString());
            }catch (Exception e){
                nextpostion.setText("");
            }




            try {
                String[] datssum=AppStart.GetInstance().pickstr[AppStart.GetInstance().nextsum].split("\\^");
                if(datssum[0].equals(gridData.get(AppStart.GetInstance().nextsum).get("IndexID").toString())){
                    inputpostiong.setText(gridData.get(AppStart.GetInstance().nextsum).get("goodsPos").toString());
                    inputsku.setText(gridData.get(AppStart.GetInstance().nextsum).get("wareGoodsCodes").toString());
                    jhsum.setText(datssum[1]);
                }
            }catch (Exception e){
                inputpostiong.setText("");
                inputsku.setText("");
                jhsum.setText("");
            }




                                       /*清空输入框的值设置焦点*/
            inputpostiong.setFocusable(true);
            inputpostiong.setFocusableInTouchMode(true);
            inputpostiong.requestFocus();
            staus=false;
            inputsku.setError(null);
        }else{
            AppStart.GetInstance().nextsum=0;
            currentsum.setText("1");
            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
            build.setMessage("已经是第一个").show();
        }



    }
    /*上一页*/
    public void previouspages(View v){


        String textpostiongs=inputpostiong.getText().toString();
        String textsku=inputsku.getText().toString();
        String sum=jhsum.getText().toString();

        textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
        textsku = TransactSQL.instance.filterSQL(textsku);
        sum = TransactSQL.instance.filterSQL(sum);

        if(textpostiongs.equals("")&&textsku.equals("")&&sum.equals("")){
            predata();
            return;
        }



        if(!textpostiongs.equals(textpostion.getText().toString())||!textsku.equals(textCode.getText().toString()) ||!sum.equals(textsum.getText().toString()))
        {
                     new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("信息不匹配是否继续？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    predata();
                                }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        }else {
            predata();
        }











/*


        nextpage--;
        if(nextpage<gridData.size()&&nextpage>=0){

            nextgood="";
            */
/*String textpostiongs=inputpostiong.getText().toString();
            String textsku=inputsku.getText().toString();
            String sum=jhsum.getText().toString();

            textpostiongs = TransactSQL.instance.filterSQL(textpostiongs);
            textsku = TransactSQL.instance.filterSQL(textsku);
            sum = TransactSQL.instance.filterSQL(sum);
*//*


             */
/*输入框值验证*//*

            if(!textpostiongs.equals("")&&!textsku.equals("")&&!sum.equals("")){
                if(textpostiongs.equals(textpostion.getText().toString())&&textsku.equals(textCode.getText().toString()))
                {
                    str[nextpage+1]=""+gridData.get(nextpage+1).get("IndexID").toString()+"^"+sum+"";
                }
            }


            textpostion.setText(gridData.get(nextpage).get("goodsPos").toString());
            textCode.setText(gridData.get(nextpage).get("wareGoodsCodes").toString());
            textTname.setText(gridData.get(nextpage).get("goodsName").toString());
            textsum.setText(gridData.get(nextpage).get("goodsNum").toString());
            currentsum.setText(""+(nextpage+1)+"");
            try{
                nextpostion.setText(gridData.get((nextpage+1)).get("goodsPos").toString());
            }catch (Exception e){
                nextpostion.setText("");
            }




            try {
                String[] datssum=str[nextpage].split("\\^");
                if(datssum[0].equals(gridData.get(nextpage).get("IndexID").toString())){
                    inputpostiong.setText(gridData.get(nextpage).get("goodsPos").toString());
                    inputsku.setText(gridData.get(nextpage).get("wareGoodsCodes").toString());
                    jhsum.setText(datssum[1]);
                }
            }catch (Exception e){
                inputpostiong.setText("");
                inputsku.setText("");
                jhsum.setText("");
            }




            */
/*清空输入框的值设置焦点*//*

            inputpostiong.setFocusable(true);
            inputpostiong.setFocusableInTouchMode(true);
            inputpostiong.requestFocus();
            inputsku.setError(null);
        }else{
            nextpage=0;
            currentsum.setText("1");
            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
            build.setMessage("已经是第一个").show();
        }
*/




    }


    /*拣货区验证*/
    public  Boolean  querjhpos(){

        String postiongtext = TransactSQL.instance.filterSQL(inputpostiong.getText().toString());
        if(postiongtext.equals("")){
            return true;
        }

        if (!textpostion.getText().toString().equals(postiongtext)) {
            inputpostiong.setError("请输入正确的货位编码！");
            selectall(inputpostiong);
            return true;
        } else {
            return false;
        }

    }
    /*货品验证*/
    public  Boolean  quergood(){

        selectall(inputsku);
        String inputskutext= TransactSQL.instance.filterSQL(inputsku.getText().toString());
        if(inputskutext.equals("")){
            /*inputsku.setError("请输入正确的货品条码！");*/
            nextgood="";
            return true;
        }



        if(inputskutext.equals(nextgood)){
            int sum=0;
            try{
                 sum= Integer.parseInt(jhsum.getText().toString());
            }catch (Exception e){}

            int djsum= Integer.parseInt(textsum.getText().toString());
            sum++;
            if(sum<=djsum){
                jhsum.setText(""+sum+"");
            }else {
                AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
                build.setMessage("拣货数量不正确！").show();
            }
            return true;
        }


        nextgood=inputskutext;
        if (!textCode.getText().toString().equals(inputskutext)) {
            inputsku.setError("货品条码不匹配！");
            nextgood="";
            selectall(inputsku);
            return  true;
        }else {
            jhsum.setText("1");
            return true;
        }

    }

    public void savaclick(View v){


        String sum=jhsum.getText().toString();
        int    datasum=Integer.parseInt(currentsum.getText().toString());
        AppStart.GetInstance().pickstr[datasum-1]=""+gridData.get(datasum-1).get("IndexID").toString()+"^"+sum+"";

        datalsit="";
        for(String col:AppStart.GetInstance().pickstr){
                datalsit+=col+"|";
        }


        String verification="";
        for(Map i:gridData){
            verification+=i.get("IndexID").toString()+"^"+i.get("goodsNum").toString()+"|";
        }


        if(!datalsit.equals(verification)){
            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
            build.setMessage("有商品未完成拣货！").show();
            return;
        }


        datalsit=datalsit.substring(0,datalsit.length()-1);

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Hashtable Param = new Hashtable<>();
                        Param.put("SPName", "SP_picking_order_goods_Upd");
                        String usernames = AppStart.GetInstance().initUserEntity();
                        int userid = AppStart.GetInstance().getUserID();
                        Param.put("pickingcode", jhorder);
                        Param.put("currUserid", "" + userid + "");
                        Param.put("currUserName", usernames);
                        Param.put("whid", ""+AppStart.GetInstance().Warehouse+"");
                        Param.put("msg", "output-varchar-500");
                        Param.put("details ", datalsit);


                        List<Hashtable> querypick = new ArrayList<Hashtable>();
                        querypick = Datarequest.GETstored(Param);
                        if (querypick.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
                            build.setMessage("拣货成功！").show();

                            Intent intent = new Intent();
                            intent.setClass(picking.this, pickingquery.class);
                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                            picking.this.finish();
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(picking.this);
                            build.setMessage(querypick.get(0).get("msg").toString()).show();
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


    public void  backmainmeue(View v){

               new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将返回主菜单！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent();
                        myIntent = new Intent(picking.this,home_page.class);
                        startActivity(myIntent);
                        picking.this.finish();
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

    public void  backpickingquery(View v){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将退出该订单拣货！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent();
                        myIntent = new Intent(picking.this, pickingquery.class);
                        startActivity(myIntent);
                        picking.this.finish();

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

            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("你将退出该订单拣货！")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            int les=AppStart.GetInstance().pickstr.length;
                            String[] cop=new String[les];

                            cop=AppStart.GetInstance().pickstr;

                            Intent myIntent = new Intent();
                            myIntent = new Intent(picking.this, pickingquery.class);
                            startActivity(myIntent);
                            picking.this.finish();

                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
