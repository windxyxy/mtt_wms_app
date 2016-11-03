package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.entity.SPEntity;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/4/6.
 */
public class replenishment_new extends buttom_state {

    private  int odds=0;
    private TextView tv_explain;
    private  String postionid="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_replenishment);

        tv_explain = (TextView) findViewById(R.id.tv_explain);
        String explain = "补货：指拣货区库存不足时，需从备货区进行货品补充。<br />" +
                "具体操作：<br />"+
                "1、用户首先需要在下方的货位编码栏中扫描拣货区<font color = '#FF0000'>缺货货位编码</font>，点击<font color = '#FF0000'>生成补货单</font>，生成补货任务。<br />" +
                "2、点击右上角补货任务查询，点击“拣”，进行拣货，拣货完成后，再点击“补”，进行补货。<br />";
        tv_explain.setText(Html.fromHtml(explain));
        tv_explain.setTextSize(18);

        EditText editText7=(EditText)findViewById(R.id.editText7);
        editText7.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    odds++;
                    if(odds%2!=0) {
                        repleclick(v);
                        EditText editText7 = (EditText) findViewById(R.id.editText7);
                        editText7.setText(editText7.getText().toString());//添加这句后实现效果
                        Spannable content = editText7.getText();
                        Selection.selectAll(content);
                    }
                    return true;
                }
                return true;
            }
        });


    }

    public  void  repleclick(View v){

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定生成补货单？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                EditText editText7 = (EditText) findViewById(R.id.editText7);
                                String jhPosition = editText7.getText().toString();
                                 /* SQL过滤*/
                                jhPosition = TransactSQL.instance.filterSQL(jhPosition);

                                if (jhPosition.equals("")) {
                                    editText7.setError("请输入正确的货位编码！");
                                    return;
                                }

                                Hashtable ParamValue = new Hashtable<>();
                                ParamValue.put("SPName", "PRO_MOVESGOODS_ADD");
                                ParamValue.put("whid", AppStart.GetInstance().Warehouse);
                                int userId = AppStart.GetInstance().getUserID();
                                ParamValue.put("userID", ""+userId+"");
                                ParamValue.put("mgotype", "2");
                                ParamValue.put("inPosFullCode", jhPosition);
                                ParamValue.put("flagId", "");
                                ParamValue.put("remark", "");
                                ParamValue.put("msg", "output-varchar-8000");
                                List<Hashtable> result = new ArrayList<Hashtable>();
                                result = Datarequest.GETstored(ParamValue);
                                if (result.get(0).get("result").toString().equals("0.0")) {
                                    AlertDialog.Builder build = new AlertDialog.Builder(replenishment_new.this);
                                    build.setMessage("生成补货单成功！").show();
                                }else {
                                    AlertDialog.Builder build = new AlertDialog.Builder(replenishment_new.this);
                                    build.setMessage(result.get(0).get("msg").toString()).show();
                                }


                            }
                        }

                )
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                )
                .show();

                }


    public void taskclick(View v){

            Intent intent = new Intent();
            intent.setClass(replenishment_new.this, reple_task.class);
            startActivity(intent);
            replenishment_new.this.finish();



    }



    public  void  repleback(View v){
        Intent intent = new Intent();
        intent.setClass(replenishment_new.this, library_management.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
    }


    public void reple_task(View v){
        Intent intent = new Intent();
        intent.setClass(replenishment_new.this, reple_task.class);
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(replenishment_new.this, library_management.class);
            startActivity(myIntent);
            replenishment_new.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
