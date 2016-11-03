package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by gfgh on 2016/4/19.
 */
public class pickingquery extends buttom_state {

    private EditText jhCode;
    private int clicksum=0;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickingquery);
        jhCode=(EditText)findViewById(R.id.editText7);

        jhCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                       clickpick(v);
                       selectall(jhCode);
                       return true;
                }
                return true;
            }
        });






    }


    public  void clickpick(View v){

        String pickingorder=jhCode.getText().toString();
        pickingorder= TransactSQL.instance.filterSQL(pickingorder);

        if(pickingorder.equals("")){
            jhCode.setError("请输入拣货单号！");
            return;
        }

        Hashtable Param= new Hashtable<>();
        Param.put("SPName", "SP_picking_order_goods_Get");
        Param.put("pickingCode", pickingorder);
        String usernames = AppStart.GetInstance().initUserEntity();
        int   userid=AppStart.GetInstance().getUserID();
        Param.put("currUserid", "" + userid + "");
        Param.put("whid", "" + AppStart.GetInstance().Warehouse + "");
        Param.put("currUserName", usernames);
        Param.put("msg", "output-varchar-100");
        List<Hashtable> querypick = new ArrayList<Hashtable>();
        querypick = Datarequest.GETstored(Param);


        if(querypick.get(0).get("result").toString().equals("0.0")){

            querypick.remove(0);
            /* 拣货单下全是虚拟商品提示*/
            if(querypick.size()==0){
                jhCode.setError("该单下全是虚拟商品！");
                return;
            }

            if(!querypick.get(0).get("pickingCode").toString().equals(AppStart.GetInstance().pickingCode)){
                AppStart.GetInstance().nextsum=0;
                AppStart.GetInstance().pickstr=new String[querypick.size()];
            }

            try{
                int les=AppStart.GetInstance().pickstr.length;
                String[] cop=new String[les];
                cop=AppStart.GetInstance().pickstr;
            }catch (Exception e){}

            AppStart.GetInstance().pickingCode=querypick.get(0).get("pickingCode").toString();

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.putExtra("order", pickingorder);
            bundle.putSerializable("listdatas", (Serializable)querypick);

            intent.putExtras(bundle);
            intent.setClass(pickingquery.this, picking.class);
            startActivity(intent);/*调用startActivity方法发送意图给系统*/
            pickingquery.this.finish();
        }else {
            /*AlertDialog.Builder build = new AlertDialog.Builder(pickingquery.this);
            build.setMessage(querypick.get(0).get("msg").toString()).show();*/
            jhCode.setError(querypick.get(0).get("msg").toString());
        }


    }




    public void  backhome_page(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(pickingquery.this,outbound_menu.class);
        startActivity(myIntent);
        pickingquery.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(pickingquery.this, outbound_menu.class);
            startActivity(myIntent);
            pickingquery.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
