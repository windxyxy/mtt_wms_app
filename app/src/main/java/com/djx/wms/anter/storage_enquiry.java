package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.entity.SPEntity;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gfgh on 2016/3/15.
 */
public class storage_enquiry extends buttom_state {

    private Button btn;
    private  List<Hashtable> listhas = new ArrayList<Hashtable>();
    private  List<Hashtable>   querydata = new ArrayList<Hashtable>();


    private  EditText editText;
    private int odd=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_enquiry);


        editText=(EditText) findViewById(R.id.editText7);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if(odd%2!=0) {
                        querygood(v);
                    }
                    return true;
                }
                return false;
            }
        });
    }





   public void  purchase_back(View v){
       Intent myIntent = new Intent();
       myIntent = new Intent(storage_enquiry.this,twolevel_menu.class);
       startActivity(myIntent);
       storage_enquiry.this.finish();
   }


    public void  querygood(View v){
        editText.setText(editText.getText().toString());//添加这句后实现效果
        Spannable content = editText.getText();
        Selection.selectAll(content);

        int   userid=AppStart.GetInstance().getUserID();

        String storageorder=editText.getText().toString();
        storageorder=TransactSQL.instance.filterSQL(storageorder);


        String SQL="select * from t_in_stock where (orderStatus =1     or orderStatus = 8 or orderStatus=3) and inStockNo='"+storageorder+"'  and  warehouseId="+AppStart.GetInstance().Warehouse+"" +
                "union select a.* from t_in_stock as a join t_in_stock_log as b on a.inStockNo = b.inStockNo and b.userID ="+userid+" where a.orderStatus = 2 and a.inStockNo='"+storageorder+"'" +
                " and  warehouseId="+AppStart.GetInstance().Warehouse+"";
        listhas= Datarequest.GetDataArrayList(SQL);
        if(storageorder.equals("")){
            editText.setError("请输入正确的入库单号");
            return;
        }


        if(listhas.size()!=0){

            if(listhas.get(0).get("orderStatus").toString().equals("8")){
                AlertDialog.Builder builds = new AlertDialog.Builder(storage_enquiry.this);
                builds.setMessage("该订单已完成入库!").show();
            }else if(listhas.get(0).get("orderStatus").toString().equals("3") || listhas.get(0).get("orderStatus").toString().equals("2")){
                querydata();
            }else if(listhas.get(0).get("orderStatus").toString().equals("1")){
                AlertDialog.Builder builds = new AlertDialog.Builder(storage_enquiry.this);
                builds.setMessage("该订单待审核!").show();
            }

        }else {
            editText.setError("该入库单号不存在或者正在入库！") ;
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            editText.setText(editText.getText().toString());//添加这句后实现效果
            Spannable contents = editText.getText();
            Selection.selectAll(contents);
        }



    }



    public void querydata(){

        if(listhas.get(0).get("orderStatus").toString().equals("3")){
            Hashtable Param= new Hashtable<>();
            Param.put("SPName", "PRO_INSTOCK_LOCK");
            /*用户ID*/
            int userid=AppStart.GetInstance().getUserID();
            Param.put("userId", ""+userid+"");
            Param.put("indexId",listhas.get(0).get("indexId").toString());
            Param.put("msg", "output-varchar-8000");

            List<Hashtable> sumbitsava= new ArrayList<Hashtable>();
            sumbitsava= Datarequest.GETstored(Param);
            if(sumbitsava.get(0).get("result").toString().equals("0.0")){
                String storageorder=editText.getText().toString();
                storageorder=TransactSQL.instance.filterSQL(storageorder);
                String SQL="select * from t_in_stock_detail  where inStockId="+listhas.get(0).get("indexId").toString()+"";
                querydata= Datarequest.GetDataArrayList(SQL);
                Intent Intent = new Intent();
                Intent.putExtra("storageorder",storageorder);

                Bundle bundle = new Bundle();
                bundle.putSerializable("datas", (Serializable)querydata);
                Intent.putExtras(bundle);
                Intent.setClass(storage_enquiry.this, storage_goods.class);
                startActivity(Intent);
                storage_enquiry.this.finish();

            }else {
                AlertDialog.Builder build = new AlertDialog.Builder(storage_enquiry.this);
                build.setMessage(sumbitsava.get(0).get("msg").toString()).show();
            }


        }


        if(listhas.get(0).get("orderStatus").toString().equals("2")){
            String storageorder=editText.getText().toString();
            storageorder=TransactSQL.instance.filterSQL(storageorder);
            String SQL="select * from t_in_stock_detail  where inStockId="+listhas.get(0).get("indexId").toString()+"";
            querydata= Datarequest.GetDataArrayList(SQL);

            Intent Intent = new Intent();
            Intent.putExtra("storageorder",storageorder);
            Bundle bundle = new Bundle();
            bundle.putSerializable("datas", (Serializable)querydata);
            Intent.putExtras(bundle);
            Intent.setClass(storage_enquiry.this, storage_goods.class);
            startActivity(Intent);
            storage_enquiry.this.finish();
        }


    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(storage_enquiry.this, twolevel_menu.class);
            startActivity(myIntent);
            storage_enquiry.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }






}




