package com.djx.wms.anter;

import android.app.AlertDialog;
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

/**
 * Created by gfgh on 2016/5/20.
 */
public class many_goods extends buttom_state {

    private EditText warecode;
    private int sum=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.many_goods);


        warecode=(EditText)findViewById(R.id.editText7);
        warecode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (creatgood(v)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

    }





    public Boolean  creatgood(View v){
        String  warecodes=TransactSQL.instance.filterSQL(warecode.getText().toString());
        if(warecodes.equals("")){
        /* AlertDialog.Builder build = new AlertDialog.Builder(many_goods.this);
            build.setMessage("请输入正确货品条码！").show();*/
            warecode.setError("请输入正确货品条码");
            return true;
        }

        List<Hashtable> listhas = new ArrayList<Hashtable>();
        String SQL="select *from v_stock where warehouseId='"+AppStart.GetInstance().Warehouse+"' and whAareType='JH'" +
                "  and wareGoodsCodes='"+warecodes+"' order by realStock asc";
        listhas = Datarequest.GetDataArrayList(SQL);
        if(listhas.size()!=0){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable)listhas);
            intent.putExtras(bundle);
            intent.setClass(many_goods.this, multiple_goods.class);
            startActivity(intent);
            many_goods.this.finish();
            return false;
        }else {
            warecode.setError("请输入正确货品条码");
            warecode.setText(warecode.getText().toString());//添加这句后实现效果
            Spannable content = warecode.getText();
            Selection.selectAll(content);
            return true;
        }




    }

    public void  backexception(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(many_goods.this, exception_menu.class);
        startActivity(myIntent);
        many_goods.this.finish();
    }



    public void clickmultiple_goods(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(many_goods.this, multiple_goods.class);
        startActivity(myIntent);
        many_goods.this.finish();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(many_goods.this, exception_menu.class);
            startActivity(myIntent);
            many_goods.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}