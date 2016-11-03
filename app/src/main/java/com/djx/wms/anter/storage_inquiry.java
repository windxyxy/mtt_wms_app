package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.entity.SPEntity;
import com.djx.wms.anter.tools.Datarequest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/3/30.
 */
public class storage_inquiry extends buttom_state{


    private List<Hashtable> listhas = new ArrayList<Hashtable>();
    private List<Hashtable> listhasdata = new ArrayList<Hashtable>();

   /* private List<Hashtable> savalist= new ArrayList<Hashtable>();*/
    private  List<Map<String, String>> savalist =new ArrayList<Map<String, String>>();
    private List<Hashtable> copygood= new ArrayList<Hashtable>();

    public  LinearLayout linear;

    public  int nextpage=0;
    private Button btnnext;
    private Button btnpre;
    private String shelforder;

    private String orders;
    private int greenstatus=0;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_inquiry);



        Button btn16=(Button)findViewById(R.id.button16);
        btn16.setClickable(false);


        RelativeLayout rela=(RelativeLayout)findViewById(R.id.main);
        linear=new LinearLayout(this);
        btnnext=(Button)findViewById(R.id.button31);
        btnpre=(Button)findViewById(R.id.button30);



        try {
            Intent intent=getIntent();
            Bundle bundle = intent.getExtras();
            savalist= (List) bundle.getSerializable("datas");

            copygood= (List) bundle.getSerializable("copygood");
            shelforder = intent.getStringExtra("shelforder");

            TextView strodorder=(TextView)findViewById(R.id.textView88);
            strodorder.setText(shelforder);
            /*回传的上架单号*/
            orders = intent.getStringExtra("order");
            String pagesum= intent.getStringExtra("pagesum");
            nextpage=Integer.parseInt(pagesum);
        }catch (Exception e){

        }




      if(copygood.size()!=0){

          if(savalist.size()!=0){

              int sum=0;
              for(Map list:copygood){
                  sum=0;
                  for(Map data:savalist){
                      if(list.get("goodSku").toString().equals(data.get("goodSku").toString())){
                          sum+=Integer.parseInt(data.get("shelefsum").toString());

                          list.put("shelefsum",""+sum+"");

                      }
                  }
              }
          }

       copygood.size();
       linear.removeAllViews();
       creadcontrol(copygood);
       rela.addView(linear);
       rela.setGravity(Gravity.CENTER);
       Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
       btnnext.setBackgroundDrawable(statusQuestionDrawable);
       btnnext.setClickable(false);

       btnpre.setBackgroundDrawable(statusQuestionDrawable);
       btnpre.setClickable(false);

       }else {
        String SQL="SELECT *FROM v_storage_inquiry  where  inStockNo=(SELECT inStockNo FROM t_in_stock  where 1=1 AND  warehouseId="+AppStart.GetInstance().Warehouse+" AND t_in_stock.orderStatus=3 ORDER BY indexId ASC OffSET 0 ROW FETCH NEXT 1 ROWS Only)";
        listhas= Datarequest.GetDataArrayList(SQL);
        if(listhas.size()!=0){
              listhasdata=listhas;
              listhas=listhasdata;
        }
      }
        //获取元素

        if(listhas.size()!=0){
            Hashtable  datas=listhas.get(0);
            TextView strodorder=(TextView)findViewById(R.id.textView88);
            strodorder.setText(listhas.get(0).get("inStockNo").toString());
            creadcontrol(listhas);
            rela.addView(linear);
            rela.setGravity(Gravity.CENTER);
        }else if(copygood.size()==0){

            Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
            btnnext.setBackgroundDrawable(statusQuestionDrawable);
            btnnext.setClickable(false);

            btnpre.setBackgroundDrawable(statusQuestionDrawable);
            btnpre.setClickable(false);

            AlertDialog.Builder build = new AlertDialog.Builder(storage_inquiry.this);
            build.setMessage("没有入库单").show();
        }








    }

/*下一页*/
public void nextpage(View v){
    nextpage++;
    String SQL="SELECT *FROM v_storage_inquiry  where  inStockNo=(SELECT inStockNo FROM t_in_stock  where 1=1  AND  warehouseId="+AppStart.GetInstance().Warehouse+"AND t_in_stock.orderStatus=3 ORDER BY indexId ASC OffSET "+nextpage+" ROW FETCH NEXT 1 ROWS Only)";
    listhas= Datarequest.GetDataArrayList(SQL);

    if(listhas.size()!=0){
        listhasdata=listhas;
        listhas=listhasdata;
    }

    if(listhas.size()==0){
        /*关闭下一页*/
        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
        btnnext.setBackgroundDrawable(statusQuestionDrawable);
        btnnext.setClickable(false);
        nextpage=nextpage-1;
        Toast.makeText(getApplicationContext(),"已是最后一个", Toast.LENGTH_SHORT).show();

        /*开启上一页*/
        Drawable status= getResources().getDrawable(R.drawable.button);
        btnpre.setBackgroundDrawable(status);
        btnpre.setClickable(true);
    }else {
        linear.removeAllViews();
        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
        btnpre.setBackgroundDrawable(statusQuestionDrawable);
        btnpre.setClickable(true);


        TextView strodorder=(TextView)findViewById(R.id.textView88);
        strodorder.setText(listhas.get(0).get("inStockNo").toString());
        creadcontrol(listhas);

    }

}

/*上一页*/
public void previouspage(View v){




    if(nextpage>0){

        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
        btnnext.setBackgroundDrawable(statusQuestionDrawable);
        btnnext.setClickable(true);

        linear.removeAllViews();
        nextpage--;
        int sumdata=nextpage+1;

        String SQL="SELECT *FROM v_storage_inquiry  where  inStockNo=(SELECT inStockNo FROM t_in_stock  where 1=1  AND  warehouseId="+AppStart.GetInstance().Warehouse+"AND t_in_stock.orderStatus=3 ORDER BY indexId ASC OffSET "+nextpage+" ROW FETCH NEXT 1 ROWS Only)";
       /* String SQL="SELECT *FROM v_storage_inquiry  where 1=1 ORDER BY ID ASC OffSET "+nextpage+" ROW FETCH NEXT "+sumdata+" ROWS Only";*/
        listhas= Datarequest.GetDataArrayList(SQL);
        TextView strodorder=(TextView)findViewById(R.id.textView88);
        strodorder.setText(listhas.get(0).get("inStockNo").toString());
        creadcontrol(listhas);
    }else {
        /*关闭上一页*/
        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
        btnpre.setBackgroundDrawable(statusQuestionDrawable);
        btnpre.setClickable(false);
        Toast.makeText(getApplicationContext(),"已是第一个", Toast.LENGTH_SHORT).show();

        /*开启下一页*/
        Drawable statu = getResources().getDrawable(R.drawable.button);
        btnnext.setBackgroundDrawable(statu);
        btnnext.setClickable(true);
    }



    }

/*跳转货品上架*/
public  void queryshelf(View v){


if(listhasdata.size()!=0||copygood.size()!=0){

    if(copygood.size()==0) {
        Hashtable ParamValue = new Hashtable<>();
        ParamValue.put("SPName", "PRO_INSTOCK_LOCK");
        String username = AppStart.GetInstance().initUserEntity();
        ParamValue.put("userName", username);

        ParamValue.put("SelectIndex", "");

        /*String ly_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        ParamValue.put("createTime", ly_time);*/

        int userID = AppStart.GetInstance().getUserID();
        ParamValue.put("IndexID", listhasdata.get(0).get("indexId"));
        ParamValue.put("msg", "output-varchar-8000");

        List<Hashtable> result = new ArrayList<Hashtable>();
        result = Datarequest.GETstored(ParamValue);


        if (result.get(0).get("result").toString().equals("0.0")) {
            SPEntity spentitys = new SPEntity();
            spentitys.ParamValue = new Hashtable<>();
            spentitys.ParamValue.put("SPName", "PRO_GetNum");
            spentitys.ParamValue.put("typeid", 8);
            spentitys.ParamValue.put("msg", "output-varchar-8000");
            List<Hashtable> inStockNo = new ArrayList<Hashtable>();
            inStockNo = Datarequest.GETstored(spentitys.ParamValue);

            orders = inStockNo.get(0).get("msg").toString();

            TextView strodorder = (TextView) findViewById(R.id.textView88);
            Intent intent = new Intent();

            intent.putExtra("pagesum", "" + nextpage + "");
            intent.putExtra("shelforder", inStockNo.get(0).get("msg").toString());
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) listhasdata);
            intent.putExtras(bundle);


            intent.setClass(storage_inquiry.this, good_shelf.class);
                 /*设置Intent的源地址和目标地址*/
            startActivity(intent);/*调用startActivity方法发送意图给系统*/
            storage_inquiry.this.finish();

        }
    }else {

        Intent intent = new Intent();
        intent.putExtra("pagesum", "" + nextpage + "");
        intent.putExtra("shelforder", orders);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) copygood);
        intent.putExtras(bundle);


        intent.setClass(storage_inquiry.this, good_shelf.class);
                 /*设置Intent的源地址和目标地址*/
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        storage_inquiry.this.finish();
        }







    }else {
        AlertDialog.Builder build = new AlertDialog.Builder(storage_inquiry.this);
        build.setMessage("锁定失败").show();
    }

}










/*动态生成表格*/
public void creadcontrol(List<Hashtable> listhas){



    for(int i=0;i<listhas.size();i++){

        Map lokmap=listhas.get(i);

        boolean switchs=false;
        try{
             for(Map op:savalist){
                 if(lokmap.get("goodSku").toString().equals(op.get("goodSku").toString()))
                 {
                     if(lokmap.get("planQty").toString().equals(op.get("shelefsum").toString())){
                         switchs=true;
                     }
                 }

               }
        }catch (Exception e){

        }


        try{
          if(lokmap.get("planQty").toString().equals(lokmap.get("shelefsum").toString())) {
           switchs=true;
          }
        }
        catch (Exception e){}

        Map  data=listhas.get(i);
        linear.setOrientation(LinearLayout.VERTICAL);
        TextView text=new TextView(this);
        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.gridborder);
        text.setBackgroundDrawable(statusQuestionDrawable);


        text.setWidth(460);
        text.setHeight(50);
        text.setGravity(Gravity.CENTER);
        text.setText(data.get("goodSku").toString());
        text.setTextSize(20);

        if(switchs){
            text.setBackgroundColor(Color.rgb(64, 230, 120));
            greenstatus++;
        }else {
            text.setBackgroundColor(Color.rgb(237, 29, 29));
        }





        TextView textone=new TextView(this);

        textone.setText(data.get("goodsName").toString());
        textone.setWidth(460);
        textone.setHeight(50);
        textone.setGravity(Gravity.CENTER);
        textone.setSingleLine(true);
        textone.setEllipsize(TextUtils.TruncateAt.END);

        textone.setBackgroundDrawable(statusQuestionDrawable);
        textone.setTextSize(20);

        LinearLayout hors=new LinearLayout(this);
        hors.setOrientation(LinearLayout.HORIZONTAL);





        TextView texttwo=new TextView(this);
        texttwo.setText(data.get("planQty").toString());
        texttwo.setWidth(230);
        texttwo.setHeight(50);
        texttwo.setGravity(Gravity.CENTER);
        Drawable statusQuestionDrawables = getResources().getDrawable(R.drawable.gridborder);
        texttwo.setBackgroundDrawable(statusQuestionDrawables);
        texttwo.setTextSize(20);

        TextView textthere=new TextView(this);

        try {
            textthere.setText(data.get("shelefsum").toString());
        }catch (Exception e){
            textthere.setText(""+0+"");
        }


        textthere.setWidth(230);
        textthere.setHeight(50);
        textthere.setGravity(Gravity.CENTER);
        textthere.setBackgroundDrawable(statusQuestionDrawables);
        textthere.setTextSize(20);

        hors.addView(texttwo);
        hors.addView(textthere);




       if(greenstatus==listhas.size()){
           Button btn29=(Button)findViewById(R.id.button28);
           Drawable status = getResources().getDrawable(R.drawable.disablebutton);
           btn29.setBackgroundDrawable(status);
           btn29.setClickable(false);


           Button btn16=(Button)findViewById(R.id.button16);
           Drawable btns = getResources().getDrawable(R.drawable.button);
           btn16.setBackgroundDrawable(btns);
           btn16.setClickable(true);



       }




        linear.addView(text);
        linear.addView(textone);
        linear.addView(hors);

       /* linear.setX(100);*/



    }


}
/*返回*/
public  void storageback(View v){

    Intent myIntent = new Intent();
    myIntent = new Intent(storage_inquiry.this, twolevel_menu.class);
    startActivity(myIntent);
    storage_inquiry.this.finish();


}

/*上架完成数据*/
public void  shelfcomplete(View v){

    Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
    btnnext.setBackgroundDrawable(statusQuestionDrawable);
    btnnext.setClickable(true);

    btnpre.setBackgroundDrawable(statusQuestionDrawable);
    btnpre.setClickable(true);



    Hashtable ParamValue =new Hashtable<>();
    ParamValue.put("SPName", "PRO_UP_GOODS_ADD");
    savalist.size();
    ParamValue.put("typeid", "0");
    ParamValue.put("ugCode", orders);
    ParamValue.put("msg", "output-varchar-8000");
    Map data=savalist.get(0);


    ParamValue.put("refid", data.get("inStockNo").toString());
    ParamValue.put("ownerId", data.get("ownerId").toString());
    ParamValue.put("warehouseId", data.get("warehouseId").toString());
   /* ParamValue.put("orderStatus", 1);*/
    String usernames=AppStart.GetInstance().initUserEntity();
    ParamValue.put("creator",usernames);
/*    ParamValue.put("executor", data.get("createUser").toString());*/
    ParamValue.put("executor", usernames);

    String ly_times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
 /*   ParamValue.put("createTime", ly_times);

    ParamValue.put("lastTime", ly_times);*/

    ParamValue.put("remark", "手持");


  /*  String str="";
    List<Map<String, String>> datasava =new ArrayList<Map<String, String>>();
    for (int ded=0;ded<copygood.size();ded++) {

     try{

     Map mapded=copygood.get(ded);
     String co=mapded.get("positionId").toString();
     datasava.add(mapded);
      }
           catch (Exception e){
     }
    }

    datasava.size();
for(int j=0;j<datasava.size();j++){
    Map mapded=datasava.get(j);
    str+=mapded.get("goodsId").toString()+"^";
    String[] datas=new String[3];
    String posfull=mapded.get("posFullCode").toString();

    datas=posfull.split("-");

    posfull=datas[0]+","+datas[1]+","+datas[2]+"-"+datas[3];

    str+=posfull+"^";

    str+=mapded.get("positionId").toString()+"^";

    str+=mapded.get("planQty").toString()+"^";

    if(j==copygood.size()-1){
        str+=mapded.get("batchNo").toString();
    }else {
        str+=mapded.get("batchNo").toString()+"|";
    }

}*/
  String str="";
    List<Map<String, String>> datasava =new ArrayList<Map<String, String>>();
    for (int ded=0;ded<savalist.size();ded++) {

     try{

     Map mapded=savalist.get(ded);
     String co=mapded.get("positionId").toString();
     datasava.add(mapded);
      }
           catch (Exception e){
     }
    }

    datasava.size();
for(int j=0;j<datasava.size();j++){
    Map mapded=datasava.get(j);
    str+=mapded.get("goodsId").toString()+"^";
    String[] datas=new String[3];
    String posfull=mapded.get("posFullCode").toString();

    datas=posfull.split("-");

    posfull=datas[0]+","+datas[1]+","+datas[2]+"-"+datas[3];

    str+=posfull+"^";

    str+=mapded.get("positionId").toString()+"^";

    str+=mapded.get("shelefsum").toString()+"^";

    if(j==savalist.size()-1){
        str+=mapded.get("batchNo").toString();
    }else {
        str+=mapded.get("batchNo").toString()+"|";
    }

}


    ParamValue.put("details", str);
    ParamValue.size();

    List<Hashtable> result= new ArrayList<Hashtable>();
    result= Datarequest.GETstored(ParamValue);

    if(result.get(0).get("result").toString().equals("0.0")) {
        AlertDialog.Builder build = new AlertDialog.Builder(storage_inquiry.this);
        build.setMessage("上架成功").show();
    /*
            Drawable status= getResources().getDrawable(R.drawable.button);
            btnpre.setBackgroundDrawable(status);
            btnpre.setClickable(true);
            btnnext.setBackgroundDrawable(statusQuestionDrawable);
            btnnext.setClickable(true);*/

        Intent myIntent = new Intent();
        myIntent = new Intent(storage_inquiry.this, storage_inquiry.class);
        startActivity(myIntent);
        storage_inquiry.this.finish();

    }else {
    AlertDialog.Builder build = new AlertDialog.Builder(storage_inquiry.this);
    build.setMessage("上架失败").show();
}

    /*明细*/
  /*  [goodsId]
    [posFullCode]
    [positionId]
    [planQty]
    [batchNo]*/


    }

}


