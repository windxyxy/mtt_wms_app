package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/5/31.
 */
public class inventory_goods extends buttom_state {
    private List<Map<String, String>> good_detail= new ArrayList<Map<String, String>>();


    private String order="",nextCode="",status="";
    private Boolean sjstatus=true;
    private int sums=0,odd=0;
    private EditText
            inputpostiong,
            inputsku,
            pdsum;

    private TextView textCode,
            textpostion,
            textTname,
            nextpostion,
            task_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_goods);

        Intent intent=getIntent();
        order=intent.getStringExtra("order");
        status=intent.getStringExtra("status");

        Bundle bundle = intent.getExtras();
        good_detail= (List) bundle.getSerializable("data");

        task_order=(TextView)findViewById(R.id.editText13);
        textTname=(TextView)findViewById(R.id.editText32);
        textCode=(TextView)findViewById(R.id.editText33);
        textpostion=(TextView)findViewById(R.id.editText34);
        nextpostion=(TextView)findViewById(R.id.textView134);

        /*货品名称滚动*/
        roll(textTname);

        task_order.setText(order);
        textTname.setText(good_detail.get(0).get("goodsName").toString());
        textCode.setText(good_detail.get(0).get("wareGoodsCodes").toString());
        textpostion.setText(good_detail.get(0).get("posCode").toString());
        try{
            nextpostion.setText(good_detail.get(1).get("posCode").toString());
        }catch (Exception e){}




        inputpostiong=(EditText)findViewById(R.id.editText35);
        inputsku=(EditText)findViewById(R.id.editText36);
        pdsum=(EditText)findViewById(R.id.editText37);



        /*回车事件复写*/
        inputpostiong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums%2!= 0) {
                        if (querypostion(inputpostiong, textpostion)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        });


        /*失去焦点触发事件*/
        inputpostiong.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                        querypostion(inputpostiong,textpostion);
                }
            }
        });



        inputsku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if (odd%2!= 0) {
                        nextCode=querycode(inputsku,pdsum,nextCode,textCode);
                        selectall(inputsku);
                    }
                    return true;
                }
                return true;
            }
        });


        inputsku.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(sjstatus){
                        nextCode=querycode(inputsku,pdsum,nextCode,textCode);
                    }
                }
            }
        });





    }




    /* 查询货位*/
    public Boolean querypostion(EditText inputpostiong,TextView textpostion){

        String Textpos = TransactSQL.instance.filterSQL(inputpostiong.getText().toString());
        String textpostions=TransactSQL.instance.filterSQL(textpostion.getText().toString());
        if(Textpos.equals("")){
            inputpostiong.setError("请输入正确的货位编码！");
            selectall(inputpostiong);
            return true;
        }

        if (textpostions.equals(Textpos)) {
            return false;
        } else {
            selectall(inputpostiong);
            inputpostiong.setError("货位编码不匹配！");
            return true;
        }

    }

    public String querycode(EditText inputsku,EditText pdsum,String nextCode,TextView textCode){
        String Code = TransactSQL.instance.filterSQL(inputsku.getText().toString());
        /*非空验证*/
        if(Code.equals("")){
            inputsku.setError("请输入正确的货品条码！");
            selectall(inputsku);
            nextCode="";
            return nextCode;
         }


        /*判断和上次条码是否一样，是的话+1*/
        if(nextCode.equals(Code)){
            int sum=0;
            try{sum=Integer.parseInt(pdsum.getText().toString());}catch (Exception e){}
            sum++;
            pdsum.setText(""+sum+"");
            return nextCode;
        }


       /*判断条码是否匹配*/
        if(Code.equals(textCode.getText().toString())){
            pdsum.setText("1");
        }else {
            inputsku.setError("请输入正确的货品条码！");
            nextCode="";
            selectall(inputsku);
            return  nextCode;
        }

        nextCode=Code;
        return  nextCode;
    }







     public  void  savaInventory(View v){

         if(querypostion(inputpostiong, textpostion)){
             inputpostiong.setError("请输入正确的货位编码！");
             return;
         }

         String Code = TransactSQL.instance.filterSQL(inputsku.getText().toString());
         if(!Code.equals(textCode.getText().toString())){
             inputsku.setError("请输入正确的货品编码！");
             return;
         }

         if(good_detail.size()==0){
             AppStart.GetInstance().pdsum=0;
             AppStart.GetInstance().cysum=0;
             dialog();
             return;
         }

         AppStart.GetInstance().cyindex=good_detail.get(0).get("indexId").toString();

                         Hashtable ParamValues =new Hashtable<>();
                         ParamValues.put("SPName", "sp_inventory_add");
                         ParamValues.put("msg", "output-varchar-500");
                         ParamValues.put("orderId", good_detail.get(0).get("orderId").toString());
                         ParamValues.put("goodsId", good_detail.get(0).get("goodsId").toString());
                         ParamValues.put("posId",good_detail.get(0).get("posId").toString());
                         ParamValues.put("num",pdsum.getText().toString());
                         String usernames = AppStart.GetInstance().initUserEntity();
                         ParamValues.put("userName",usernames);

                         List<Hashtable> result = new ArrayList<Hashtable>();
                         result = Datarequest.GETstored(ParamValues);
                         if(result.get(0).get("result").toString().equals("0.0")){

                             String orderid=good_detail.get(0).get("orderId").toString();
                             List<Hashtable> gooddetail = new ArrayList<Hashtable>();
                             if(status.equals("1")){
                                 /*全盘下一页*/
                                 AppStart.GetInstance().pdsum++;
                                 String SQL = "select  * from v_inventory_detail where orderId = '"+orderid+"' order by routeValue  OFFSET "+AppStart.GetInstance().pdsum+" ROW FETCH NEXT 2 Rows Only; ";
                                 gooddetail = Datarequest.GetDataArrayList(SQL);
                                 if(gooddetail.size()==0){
                                     AppStart.GetInstance().pdsum=0;
                                     dialog();
                                 }

                             }else {
                                  /*差异盘点下一页*/
                                  /*AppStart.GetInstance().cysum++;*/
                                 String SQL = "select  * from v_inventory_detail where orderId = '"+orderid+"' and beforeCheckNum <> lastCheckNum and checkTimes > 0 order by routeValue  OFFSET "+AppStart.GetInstance().cysum+" ROW FETCH NEXT 2 Rows Only; ";
                                 gooddetail = Datarequest.GetDataArrayList(SQL);
                                 if(gooddetail.size()==0){
                                     AppStart.GetInstance().cysum=0;
                                     dialog();
                                 }else{

                                     /*根据上一个的index来判断下一个的盘点数据*/
                                     if(AppStart.GetInstance().cyindex.equals(gooddetail.get(0).get("indexId").toString()))
                                     {
                                         AppStart.GetInstance().cysum++;
                                         String SQLS= "select  * from v_inventory_detail where orderId = '"+orderid+"' and beforeCheckNum <> lastCheckNum and checkTimes > 0 order by routeValue  OFFSET "+AppStart.GetInstance().cysum+" ROW FETCH NEXT 2 Rows Only; ";
                                         gooddetail = Datarequest.GetDataArrayList(SQLS);
                                         /*根据返回弹出提示*/
                                         if(gooddetail.size()==0){AppStart.GetInstance().cysum=0;dialog();}
                                     }

                                 }

                             }



                             try{
                                 good_detail=new ArrayList<Map<String, String>>();
                                 for(Map data:gooddetail){
                                     good_detail.add(data);
                                 }
                                 /* 成功下一页*/
                                 good_detail.size();
                                 task_order.setText(order);
                                 textTname.setText(good_detail.get(0).get("goodsName").toString());
                                 textCode.setText(good_detail.get(0).get("wareGoodsCodes").toString());
                                 textpostion.setText(good_detail.get(0).get("posCode").toString());
                                 try{
                                     nextpostion.setText(good_detail.get(1).get("posCode").toString());
                                 }catch (Exception e){
                                     nextpostion.setText("");
                                 }

                                 inputpostiong.setText("");
                                 inputsku.setText("");
                                 pdsum.setText("");
                                 sjstatus=false;
                                 inputpostiong.setFocusable(true);
                                 inputpostiong.setFocusableInTouchMode(true);
                                 inputpostiong.requestFocus();
                                 nextCode="";
                                 sjstatus=true;
                              }catch(Exception e){}



                         } else {
                             AlertDialog.Builder build = new AlertDialog.Builder(inventory_goods.this);
                             build.setMessage(result.get(0).get("msg").toString()).show();
                         }

     }




    /*返回*/
    public void backinventory_task(View v){

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否继续退出！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent();
                        myIntent = new Intent(inventory_goods.this, inventory_task.class);
                        startActivity(myIntent);
                        inventory_goods.this.finish();

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

      public void dialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("盘点已完成是否退出！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setClass(inventory_goods.this, inventory_task.class);
                        startActivity(intent);
                        inventory_goods.this.finish();
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
                    .setMessage("是否继续退出！")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent();
                            myIntent = new Intent(inventory_goods.this, inventory_task.class);
                            startActivity(myIntent);
                            inventory_goods.this.finish();

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
