package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.djx.wms.anter.entity.SPEntity;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gfgh on 2016/3/16.
 */
public class warehousing_check extends  buttom_state {
    Handler handler = null;
    Message msg;


    private GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器
    private int sums=0;
    /*采购查询数据*/
    private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();
    /*  保存货品数据*/
    private  List<Map<String, String>> Storage =new ArrayList<Map<String, String>>();

    private int op=1;
    private int Storagesum=1;
    private String disstory=null;
    private String EditTextsku;
    private  List<Hashtable> listhas = new ArrayList<Hashtable>();
    private int datasum;

    /*预采购开关*/
    private boolean Pre_purchase=true;
    /*重复数据*/
    private int repeat=0;
    /*数据查询判断*/
    private int dataquery=0;


    /*采购单号*/
    private  String purchaseno;

    private Map<String,String> datamap = new HashMap<String,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehousing_check);

        Button btn21=(Button)findViewById(R.id.button21);
        btn21.setClickable(false);
       /* btn21.setBackground();*/


        //输入框值change事件
        EditText changetext=(EditText)findViewById(R.id.editText8);
        changetext.addTextChangedListener(
         new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                Button btn21=(Button)findViewById(R.id.button21);
                Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.border);
                btn21.setClickable(false);
                btn21.setBackgroundDrawable(statusQuestionDrawable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,   int after) {         }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,     int count) {
            }
        });





        Intent intent=getIntent();
       /* String purchaseorder=intent.getStringExtra("purchaseorders");*/
        Bundle bundle = intent.getExtras();
        listData= (List) bundle.getSerializable("data");
        purchaseno= listData.get(0).get("purchaseNo").toString();


        TextView TextViews=(TextView)findViewById(R.id.textView24);
        TextViews.setText(purchaseno);



        EditText editText=(EditText)findViewById(R.id.editText8);

        /*回车事件复写*/
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    LoginClickEvents(v);
                    return false;
                }
                return false;
            }
        });
    }






    public void LoginClickEvents(View v)
    {
        Button btn21=(Button)findViewById(R.id.button21);

        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);


        EditText EditTexts=(EditText)findViewById(R.id.editText8);
        TextView goodsname=(TextView)findViewById(R.id.textView27);
        TextView goodsSKU=(TextView)findViewById(R.id.textView29);
        TextView goodssum=(TextView)findViewById(R.id.textView31);

        EditTextsku=EditTexts.getText().toString();
        EditTextsku= TransactSQL.instance.filterSQL(EditTextsku);

        if(!EditTextsku.equals("")){
            int ordersum=0;
            boolean code;
            if(op==1){
                code=true;
                op++;
            }else {
                code=false;
                if(disstory!=null){
                    if(disstory.equals(EditTextsku)){
                        code=true;
                    }
                }
            }


            disstory=EditTextsku;
            EditText sum=(EditText) findViewById(R.id.editText9);
           /*查询传递数据*/
            for(int i = 0; i < listData.size(); i++) {
                Map map = listData.get(i);
                Set set = map.keySet();
                Iterator it = set.iterator();
                String goodsSku="";
                goodsSku= map.get("goodSku").toString();
                if(goodsSku.equals(EditTextsku)){
                    goodsname.setText(map.get("goodsName").toString());
                    goodsSKU.setText(map.get("goodSku").toString());
                    goodssum.setText(map.get("planQty").toString());
                    ordersum++;

                    if(code){
                        sums++;
                        sum.setText("" + sums + "");
                        btn21.setClickable(true);
                        btn21.setBackgroundDrawable(statusQuestionDrawable);
                    }else {
                        sums=1;
                        sum.setText("" + sums + "");

                        btn21.setBackgroundDrawable(statusQuestionDrawable);
                        btn21.setClickable(true);
                    }

                }
            }


        /*当传递数据里没有时，查询数据库*/
            if(ordersum==0){

                List<Hashtable> listhas = new ArrayList<>();
                String SQL="select *from v_android_purchase  where goodSku='"+EditTextsku+"' and purchaseNo='"+purchaseno+"'";
                listhas= Datarequest.GetDataArrayList(SQL);
                if(listhas.size()==0){

                    new AlertDialog.Builder(this,sums)
                            .setTitle("提示")
                            .setMessage("该订单没有此货品!")
                            .setPositiveButton("继续操作", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    List<Hashtable> notsku = new ArrayList<>();
                                    String SQL="select *from v_purchase_notgoods where goodsSku='"+EditTextsku+"'";
                                    notsku= Datarequest.GetDataArrayList(SQL);
                                    if(notsku.size()!=0){
                                        TextView goodsname=(TextView)findViewById(R.id.textView27);
                                        TextView goodsSKU=(TextView)findViewById(R.id.textView29);
                                        TextView goodssum=(TextView)findViewById(R.id.textView31);
                                        goodsname.setText(notsku.get(0).get("goodsName").toString());
                                        goodsSKU.setText(notsku.get(0).get("goodsSku").toString());

                                        notsku.get(0).put("goodSku", notsku.get(0).get("goodsSku").toString());
                                        notsku.get(0).put("goodsId",notsku.get(0).get("indexId").toString());

                                        goodssum.setText("");
                                        Map  quermap=notsku.get(0);
                                        quermap.put("planQty", "");
                                        listData.add(quermap);
                                        EditText sum=(EditText) findViewById(R.id.editText9);
                                        Button btn21=(Button)findViewById(R.id.button21);

                                        sums=1;
                                        sum.setText("" + sums + "");

                                        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
                                        btn21.setBackgroundDrawable(statusQuestionDrawable);
                                        btn21.setClickable(true);
                                    }else {

                                        AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
                                        build.setMessage("货品不存在！").show();
                                    }


                                }
                            })
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                            /* this.finish();*/
                                }
                            })
                            .show();




                }else{
                    Map  quermap=listhas.get(0);
                    listData.add(quermap);
                    goodsname.setText(listhas.get(0).get("goodsName").toString());
                    goodsSKU.setText(listhas.get(0).get("goodSku").toString());
                    goodssum.setText(listhas.get(0).get("planQty").toString());

                    if(code){
                        sums++;
                        sum.setText("" + sums + "");
                        btn21.setBackgroundDrawable(statusQuestionDrawable);
                        btn21.setClickable(true);
                    }else {
                        sums=1;
                        sum.setText("" + sums + "");
                        btn21.setBackgroundDrawable(statusQuestionDrawable);
                        btn21.setClickable(true);
                    }
                   /* ----------------------------------------------------*/

                }
            }
        }else {
            AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
            build.setMessage("货品编码不能为空").show();
        }



    }


    //保存入库核对单
    public void savagoods(View v) {

    EditText EditTextskus=(EditText)findViewById(R.id.editText8);
    EditText editText10=(EditText)findViewById(R.id.editText10);
    String Text= EditTextskus.getText().toString();

        /*验证sku是否为空*/
        if(Text.equals("")){
            AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
            build.setMessage("请输入货品编号").show();
        }

     /*验证批次号是否为空*/
        boolean editbool=false;
     if(!editText10.getText().toString().equals("")){
         editbool=true;
     }else {
         AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
         build.setMessage("批次号不能为空").show();
     }







    if(Text!=null && editbool==true){
    EditText good=(EditText)findViewById(R.id.editText9);
    EditText EditTexts=(EditText)findViewById(R.id.editText8);
    String Editdata=EditTexts.getText().toString();
    Editdata= TransactSQL.instance.filterSQL(Editdata);
    Torepeat();




    /* 根据数据源判断，页面传递数据是否存在*/
    if(dataquery==0){
        List<Hashtable> listhas = new ArrayList<Hashtable>();
        String SQL="select *from v_android_purchase  where goodSku='"+Editdata+"' and purchaseNo='"+purchaseno+"'";
        listhas= Datarequest.GetDataArrayList(SQL);

        for(int i = 0; i < listhas.size(); i++) {
            Map map = listhas.get(i);
            String goodsSku= map.get("goodSku").toString();

           /* 判断输入的SKU是否和查询的数据中有一样的值*/
            if(goodsSku.equals(Editdata)){
                /*遍历保存数据，是否有重复的数据*/
                for(int op=0;op<Storage.size();op++){
                    Map maps = Storage.get(op);
                    String Storagegood=maps.get("goodSku").toString();

                    /*如果保存数据存在，那么修改实际数量*/
                    if(Storagegood.equals(goodsSku)){
                        Storage.size();
                     /* 判断实际采购数量和与采购是否相等*/

                            String goodtext= TransactSQL.instance.filterSQL(good.getText().toString());
                            int sums=Integer.parseInt(goodtext);
                            int mapssum=Integer.parseInt(maps.get("planQtys").toString());
                            int sum=sums+mapssum;
                            maps.put("planQtys",""+sum+"");
                            EditText batch=(EditText)findViewById(R.id.editText10);
                            maps.put("batchNo",""+batch.getText().toString()+"");
                            repeat++;
                            break;


                    }else {
                           repeat=0;
                         }
                }

                /*如果保存数据不存在，那么添加到保存数据*/
                if(repeat==0) {
                    EditText batch=(EditText)findViewById(R.id.editText10);
                    map.put("batchNo",""+batch.getText().toString()+"");
                    Storage.add(map);
                    Storage.get((Storage.size()-1)).put("planQtys", good.getText().toString());

                }
                /*第一次保存数据，那么添加到保存数据*/
                if(Storage.size()==0){
                    EditText batch=(EditText)findViewById(R.id.editText10);
                    map.put("batchNo",""+batch.getText().toString()+"");
                    Storage.add(map);
                    Storage.get(0).put("planQtys",good.getText().toString());
                }
            }

        }
    }






       AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
       build.setMessage("保存成功").show();
       Storage.size();
       listData.size();
       Storage.size();

    }




 }



    public void generateStorage(View v){
        int sums=0;

        String planQtys = Storage.get(0).get("planQtys").toString();

        if(!planQtys.equals("")){
            for(int i = 0; i < Storage.size(); i++) {
                Map map = Storage.get(i);
                String goodsplanQtys= map.get("planQtys").toString();
                String goodsplanQty= map.get("planQty").toString();
                if(!goodsplanQtys.equals(goodsplanQty)){
                    sums++;
                }
            }

        }

       if(sums!=0){
           new AlertDialog.Builder(this)
                   .setTitle("提示")
                   .setMessage("下面有货品实到货量和预采购量不匹配")

                   .setPositiveButton("继续保存", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           // TODO Auto-generated method stub

                           String[] whether=Submit();

                           if(whether[0].equals("0.0")){
                               AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
                               build.setMessage("保存成功").show();


                               Intent myIntent = new Intent();
                               myIntent = new Intent(warehousing_check.this, storage_enquiry.class);
                               startActivity(myIntent);
                               warehousing_check.this.finish();
                           }else {
                               AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
                               build.setMessage(whether[1]).show();
                               AlertDialog.Builder builds = new AlertDialog.Builder(warehousing_check.this);

                               build.setMessage("保存失败").show();
                           }


                       }
                   })
                   .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                           /*this.finish();*/
                       }
                   })
                   .show();

       }else {
           String[] whether=Submit();
           if(whether[0].equals("0.0")){
               AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
               build.setMessage("保存成功").show();

               Intent myIntent = new Intent();
               myIntent = new Intent(warehousing_check.this, storage_enquiry.class);
               startActivity(myIntent);
               warehousing_check.this.finish();
           }else {
               AlertDialog.Builder build = new AlertDialog.Builder(warehousing_check.this);
               build.setMessage(whether[1]).show();
           }


       }
    }

    /* 提交数据*/
    public  String[] Submit(){

        SPEntity   spentitys = new SPEntity();
        spentitys.ParamValue =new Hashtable<>();

        spentitys.ParamValue.put("SPName", "PRO_GetNum");
        spentitys.ParamValue.put("typeid", 6);
        spentitys.ParamValue.put("msg", "output-varchar-100");
        List<Hashtable> inStockNo = new ArrayList<Hashtable>();
        inStockNo= Datarequest.GETstored(spentitys.ParamValue);


        SPEntity   spentity = new SPEntity();
        spentity.ParamValue =new Hashtable<>();


        spentity.ParamValue.put("SPName", "PRO_INSTOCK_ADD");
        spentity.ParamValue.put("msg", "output-varchar-8000");

           /*未获取*/
        spentity.ParamValue.put("inStockNo", inStockNo.get(0).get("msg").toString());
           /*获取*/
        spentity.ParamValue.put("ownerId", listData.get(0).get("ownerId").toString());

           /*仓库ID*/
        spentity.ParamValue.put("warehouseId", listData.get(0).get("warehouseId").toString());

           /*供应商ID*/
        spentity.ParamValue.put("providerId",  listData.get(0).get("providerId").toString());


           /*物流公司ID*/
        spentity.ParamValue.put("logisticsId", listData.get(0).get("logisticsId").toString());
           /*物流单号*/
        spentity.ParamValue.put("logisticsNo", listData.get(0).get("logisticsNo").toString());

           /*创建时间*/
      /*  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式*/
        String ly_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        spentity.ParamValue.put("createTime", ly_time);

        int userID=AppStart.GetInstance().getUserID();
           /*userID*/
        spentity.ParamValue.put("createUser", userID);
             /*业务类型*/
        spentity.ParamValue.put("orderType", 1);

        spentity.ParamValue.put("refOrderType", 1);

            /*关联单号*/
        spentity.ParamValue.put("refOrderId", purchaseno);

              /*状态*/
        spentity.ParamValue.put("orderStatus", 3);

              /*最后修改时间*/
        spentity.ParamValue.put("lastTime",listData.get(0).get("lastTime").toString());

             /*标记*/
        spentity.ParamValue.put("flagId",listData.get(0).get("flagId").toString());


           /*备注*/
        spentity.ParamValue.put("remark","手持");

           /*批次号*/
        spentity.ParamValue.put("batchNo",listData.get(0).get("batchCode").toString());

        spentity.ParamValue.size();


        String str="";
        for (int ded=0;ded<Storage.size();ded++) {
            Map mapded=Storage.get(ded);
            str+=mapded.get("goodsId").toString()+"^";
            str+=mapded.get("planQtys").toString()+"^";
            str+=mapded.get("batchNo").toString()+"^";
            EditText batch=(EditText)findViewById(R.id.editText10);
            if(ded==Storage.size()-1){
                str+="手持";
            }else {
                str+="手持|";
            }

        }
           /*明细*/
        spentity.ParamValue.put("details",str);
        List<Hashtable> datasucess = new ArrayList<Hashtable>();
        datasucess= Datarequest.GETstored(spentity.ParamValue);
        String[] datsstats=new String[2];
        datsstats[0]=datasucess.get(0).get("result").toString();
        datsstats[1]=datasucess.get(0).get("msg").toString();

        return datsstats;
    }




    public void Torepeat(){

        EditText good=(EditText)findViewById(R.id.editText9);
        EditText EditTexts=(EditText)findViewById(R.id.editText8);
        String Editdata=EditTexts.getText().toString();
        Editdata= TransactSQL.instance.filterSQL(Editdata);

        //实体比较
        for(int i = 0; i < listData.size(); i++) {
            Map map = listData.get(i);
            Storage.size();
            String goodsSku= map.get("goodSku").toString();


         /*判断输入的SKU是否和查询的数据中有一样的值*/
            if(goodsSku.equals(Editdata)){

                dataquery++;


            /*遍历保存数据，是否有重复的数据*/
                for(int op=0;op<Storage.size();op++){
                    Map maps = Storage.get(op);
                    String Storagegood=maps.get("goodSku").toString();

                 /*如果保存数据存在，那么修改实际数量*/
                    if(Storagegood.equals(goodsSku)){
                        Storage.size();
                      /*判断实际采购数量和与采购是否相等*/

                        String goodtext= TransactSQL.instance.filterSQL(good.getText().toString());
                        int sums=Integer.parseInt(goodtext);
                        int mapssum=Integer.parseInt(maps.get("planQtys").toString());

                        int sum=sums+mapssum;
                        EditText batch=(EditText)findViewById(R.id.editText10);
                        maps.put("batchNo",""+batch.getText().toString()+"");
                        maps.put("planQtys",""+sum+"");
                        repeat++;
                        break;

                    }else {
                        repeat=0;
                    }
                }

           /*如果保存数据不存在，那么添加到保存数据*/
                if(repeat==0) {
                    EditText batch=(EditText)findViewById(R.id.editText10);
                    map.put("batchNo",""+batch.getText().toString()+"");
                    Storage.add(map);
                    Storage.get((Storage.size()-1)).put("planQtys", good.getText().toString());

                }
            /*第一次保存数据，那么添加到保存数据*/
                if(Storage.size()==0){
                    EditText batch=(EditText)findViewById(R.id.editText10);
                    map.put("batchNo",""+batch.getText().toString()+"");
                    Storage.add(map);
                    Storage.get(0).put("planQtys",good.getText().toString());
                }
            }

        }

    }


    public void warhouse(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(warehousing_check.this, storage_enquiry.class);
        startActivity(myIntent);
        warehousing_check.this.finish();

    }
}
