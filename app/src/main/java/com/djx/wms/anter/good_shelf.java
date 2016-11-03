package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/3/31.
 */
public class good_shelf  extends buttom_state{



    private List<Map<String, String>> listData= new ArrayList<Map<String, String>>();
    private int sum=0;
    String order;
    private int savasum=1;
/*    private boolean savasum=true;*/
    private String  pagesum;

    private List<Map<String, String>> goodsava =new ArrayList<Map<String, String>>();
    private List<Hashtable> listhas = new ArrayList<Hashtable>();

    private List<Map<String, String>> copygood =new ArrayList<Map<String, String>>();

    private String shelforder;
    int tolta=0;
    private String positionId;
    private String nextsku="";
    private String goodpostion="";
    private int odds=0;
    private String text16="";
    private  int backsum=1;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.good_shelf);

        Button btn=(Button)findViewById(R.id.button14);
        btn.setClickable(false);

        EditText editText15=(EditText)findViewById(R.id.editText15);
        editText15.setFocusable(true);
        editText15.setFocusableInTouchMode(true);
        editText15.requestFocus();



        Intent intent=getIntent();
        shelforder = intent.getStringExtra("shelforder");
        pagesum= intent.getStringExtra("pagesum");


        EditText edorder=(EditText)findViewById(R.id.editText12);
        edorder.setText(shelforder);
        Bundle bundle = intent.getExtras();
        listData= (List) bundle.getSerializable("data");

        copygood=listData;

        order= listData.get(0).get("inStockNo").toString();








        EditText stororder=(EditText)findViewById(R.id.editText11);
        stororder.setText(order);

        EditText editText16=(EditText)findViewById(R.id.editText16);
        //输入框值change事件
        editText16.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {

                        EditText shelfsum=(EditText)findViewById(R.id.editText16);
                        EditText  surplus=(EditText)findViewById(R.id.editText17);
                        EditText  editText13=(EditText)findViewById(R.id.editText13);



                        if(!shelfsum.getText().toString().equals("") && tolta>0){
                            int shelf=Integer.parseInt(shelfsum.getText().toString());
                            int surplusint=Integer.parseInt(surplus.getText().toString());

                            if((tolta-shelf)>=0){
                                surplus.setText(""+(tolta-shelf)+"");
                            }else{
                                shelfsum.setText(text16);
                                AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                                build.setMessage("上架数量不能大于入库数量").show();
                            }
                        }


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        EditText editText16=(EditText)findViewById(R.id.editText16);
                        text16=editText16.getText().toString();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });



        EditText editText=(EditText)findViewById(R.id.editText13);
        //输入框值change事件
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        sum = 0;
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

        /*回车事件复写*/
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                  odds++;
                 if(odds%2!=0){

                     EditText editText13 = (EditText) findViewById(R.id.editText13);
                     editText13.setText(editText13.getText().toString());//添加这句后实现效果
                     Spannable content = editText13.getText();
                     Selection.selectAll(content);
                     EditText editText15 = (EditText) findViewById(R.id.editText15);

                     if(query()){
                        return true;
                     }
                     nextsku=editText13.getText().toString();
                     goodpostion=editText15.getText().toString();

                  }

                return true;
                }
                return true;
            }
        });

    }


    public  Boolean   datasur(){
        EditText editText13 = (EditText) findViewById(R.id.editText13);
        EditText editText16 = (EditText) findViewById(R.id.editText16);
        int datasum = 0;
        for (Map cos : goodsava) {

           if(editText13.getText().toString().equals(cos.get("goodSku").toString())){
               datasum+=Integer.parseInt(cos.get("shelefsum").toString());
               int sum=Integer.parseInt(cos.get("planQty").toString());
               if(sum==datasum){
                   AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                   build.setMessage("上架数量不能大于入库数量").show();
                   editText16.setText("");
                   return true;
               }
           }

        }


       return false;
    }

    private  Boolean query(){


        if(datasur()){
            return true;
        }


        EditText editText=(EditText)findViewById(R.id.editText13);
        String Value=editText.getText().toString();

        EditText editText15=(EditText)findViewById(R.id.editText15);
        String Value15=editText15.getText().toString();

        if(!nextsku.equals("")){
            if(nextsku.equals(Value)&&goodpostion.equals(Value15)){
                EditText editText16=(EditText)findViewById(R.id.editText16);
                int sum=Integer.parseInt(editText16.getText().toString()) ;
                sum=sum+1;
                editText16.setText(""+sum+"");
                return false;
            }
        }

        boolean datastatus=true;

        for (Map i : listData) {
            if(i.get("goodSku").toString().equals(Value)){
                TextView editgoodSku=(TextView)findViewById(R.id.editText14);
                EditText shelfsum=(EditText)findViewById(R.id.editText16);
                EditText  surpluss=(EditText)findViewById(R.id.editText17);

                sum++;
                int surpsum;
                try{
                     surpsum=Integer.parseInt(i.get("surplus").toString());
                }catch (Exception e){
                     surpsum=Integer.parseInt(i.get("planQty").toString());
                }

                if(sum<=surpsum){
                    int cos=surpsum-sum;
                  /*  tolta=(surpsum-sum)+sum;*/
                    tolta=surpsum;
                    EditText  sur=(EditText)findViewById(R.id.editText17);
                    sur.setText(""+cos+"");
                    shelfsum.setText(""+sum+"");
                    editgoodSku.setText(i.get("goodsName").toString());
                    break;
                }else {
                    datastatus=false;
                    break;
                }

            }else {
                datastatus=false;
            }
        }
             if(!datastatus){
                  AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                  build.setMessage("该入库单没有此货品").show();
                  return true;
             }


        return false;





    }


    public void goodback(View v){
        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        intent.putExtra("pagesum",pagesum);
        intent.putExtra("shelforder", order);



        bundle.putSerializable("datas", (Serializable) goodsava);
        bundle.putSerializable("copygood", (Serializable) copygood);

        bundle.putSerializable("order", (Serializable) shelforder);
        intent.putExtras(bundle);


        intent.setClass(good_shelf.this, storage_inquiry.class);
                 /*设置Intent的源地址和目标地址*/
        startActivity(intent);/*调用startActivity方法发送意图给系统*/
        good_shelf.this.finish();


    }

    public  void shelfsava(View v){

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您将保存此上架商品信息")

                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        EditText editText = (EditText) findViewById(R.id.editText13);
                        String Value = editText.getText().toString();
                        Value = TransactSQL.instance.filterSQL(Value);

                        if (Value == null || Value.trim().equals("")) {
                            editText.setError("请输入SKU！");
                            return;
                        }

                        TextView textgoods= (TextView) findViewById(R.id.editText14);
                        if (textgoods.getText().toString().equals("")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                            build.setMessage("商品名不能为空！").show();
                            return;
                        }

                        EditText editText15 = (EditText) findViewById(R.id.editText15);
                        String Text15 = editText15.getText().toString();


                        if (Text15 == null || Text15.equals("")) {
                            editText15.setError("请输入货位！");
                            return;
                        } else {
                            Hashtable ParamValue = new Hashtable<>();
                            ParamValue.put("SPName", "PRO_UP_GOODS_CHECK_POSITION");
                            String username = AppStart.GetInstance().initUserEntity();
                            ParamValue.put("warehouseId", AppStart.GetInstance().Warehouse);
                            String[] arry = new String[3];
                            arry = Text15.split("-");

                            arry[2] = arry[2] + "-" + arry[3];

                            ParamValue.put("area", arry[0]);
                            ParamValue.put("shelf", arry[1]);
                            ParamValue.put("position", arry[2]);
                            ParamValue.put("msg", "output-varchar-100");

                            List<Hashtable> result = new ArrayList<Hashtable>();
                            result = Datarequest.GETstored(ParamValue);


                            if (result.get(0).get("result").toString().equals("0.0")) {
                                positionId = result.get(0).get("msg").toString();
                            } else {
                                editText15.setError("货位不存在！");
                                return;
                            }


                        }


                        EditText editText16 = (EditText) findViewById(R.id.editText16);
                        String Text16 = editText16.getText().toString();
                        if (Text16 == null || Text16.trim().equals("")) {
                            editText16.setError("请输入数量！");
                            return;
                        }


                        datasur();

                        for (Map j : goodsava) {
                            if (j.get("goodSku").toString().equals(Value)) {
                                EditText shelefsum = (EditText) findViewById(R.id.editText16);
                                int shelef = Integer.parseInt(shelefsum.getText().toString());
                                int sava = Integer.parseInt(j.get("shelefsum").toString());
                                int planQty = Integer.parseInt(j.get("planQty").toString());

                                if (shelef + sava > planQty) {
                                    AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                                    build.setMessage("上架数量不能大于入库数量").show();
                                    return;
                                }

                            }

                        }


                        boolean breastatus = false;
                        for (Map i : listData) {


                            if (i.get("goodSku").equals(Value)) {
                               /* EditText shelefsum = (EditText) findViewById(R.id.editText16);*/


                                EditText shelefsum = (EditText) findViewById(R.id.editText16);

                                savasum++;
                                if (savasum == 2) {
                                    int shelef = Integer.parseInt(shelefsum.getText().toString());
                                    int plan = Integer.parseInt(i.get("planQty").toString());

                                    Map<String, String> col = new HashMap<String, String>();
                                    col.putAll(i);
                                    i.put("surplus", "" + (plan - shelef) + "");
                                    backsum = plan - shelef;
                                    listData.size();
                                    col.put("shelefsum", "" + shelef + "");

                                    EditText FullCode = (EditText) findViewById(R.id.editText15);
                                    String ValueS = FullCode.getText().toString();

                                    col.put("posFullCode", ValueS);
                                    col.put("positionId", positionId);
                                    goodsava.add(col);
                                    break;
                                }


                                for (Map m : goodsava) {
                                    if (m.get("posFullCode").toString().equals(Text15) && m.get("goodSku").toString().equals(Value)) {
                                        int shelef = Integer.parseInt(shelefsum.getText().toString());
                                        int sava = Integer.parseInt(m.get("shelefsum").toString());
                                        m.put("shelefsum", "" + (sava + shelef) + "");

                                       /* Map<String,String> col=new HashMap<String, String>();
                                        col.putAll(i);*/

                                        EditText FullCode = (EditText) findViewById(R.id.editText15);
                                        String ValueS = TransactSQL.instance.filterSQL(FullCode.getText().toString());
                                        ValueS = FullCode.getText().toString();
                                        m.put("posFullCode", ValueS);
                                        m.put("positionId", positionId);
                                        breastatus = true;
                                    }
                                }

                                if (breastatus) {
                                    break;
                                }

                                Boolean los = false;
                                for (Map m : goodsava) {
                                    if (!m.get("goodSku").toString().equals(Value) || !m.get("posFullCode").toString().equals(Text15)) {


                                        Iterator it = i.keySet().iterator();
                                        Map<String, String> col = new HashMap<String, String>();


                                        int plan = 0;
                                        try {
                                            plan = Integer.parseInt(i.get("surplus").toString());
                                        } catch (Exception e) {
                                            plan = Integer.parseInt(i.get("planQty").toString());
                                        }

                                        int shelef = Integer.parseInt(shelefsum.getText().toString());
                                      /*  i.put("planQty", "" + (plan - shelef) + "");*/
                                        i.put("surplus", "" + (plan - shelef) + "");
                                        backsum = plan - shelef;
                                        col.putAll(i);

                                        col.put("shelefsum", "" + shelef + "");
                                        EditText FullCode = (EditText) findViewById(R.id.editText15);
                                        String ValueS = FullCode.getText().toString();
                                        col.put("posFullCode", ValueS);
                                        col.put("positionId", positionId);
                                        goodsava.add(col);
                                        break;
                                    }
                                }

/*

                                if (los) {
                                    break;
                                }
*/




                                /*for (Map m : goodsava) {
                                    if (m.get("goodSku").toString().equals(Value)) {
                                        int shelef = Integer.parseInt(shelefsum.getText().toString());
                                        int sava = Integer.parseInt(m.get("shelefsum").toString());
                                        m.put("shelefsum", "" + (sava + shelef) + "");

                                        EditText FullCode = (EditText) findViewById(R.id.editText15);
                                        String ValueS = TransactSQL.instance.filterSQL(FullCode.getText().toString());
                                         ValueS = FullCode.getText().toString();
                                        m.put("posFullCode", ValueS);
                                        m.put("positionId", positionId);
                                        breastatus = true;
                                    }
                                }

                                if (breastatus) {
                                    break;
                                }

                                for (Map m : goodsava) {
                                    if (!m.get("goodSku").toString().equals(Value)) {
                                        int shelef = Integer.parseInt(shelefsum.getText().toString());
                                        i.put("shelefsum", "" + shelef + "");

                                        EditText FullCode = (EditText) findViewById(R.id.editText15);
                                        String ValueS = FullCode.getText().toString();
                                        i.put("posFullCode", ValueS);
                                        i.put("positionId", positionId);
                                        goodsava.add(i);
                                        break;
                                    }
                                }
                                if (savasum == 1) {
                                    int shelef = Integer.parseInt(shelefsum.getText().toString());
                                    i.put("shelefsum", "" + shelef + "");

                                    EditText FullCode = (EditText) findViewById(R.id.editText15);
                                    String ValueS = FullCode.getText().toString();

                                    i.put("posFullCode", ValueS);
                                    i.put("positionId", positionId);

                                    goodsava.add(i);
                                    savasum++;
                                }
*/
                            }

                        }


                        AlertDialog.Builder build = new AlertDialog.Builder(good_shelf.this);
                        build.setMessage("保存成功").show();
                        goodsava.size();


                        int cot = 0;
                        for (Map s : listData) {
                            int lost = 0;
                            for (Map lo : goodsava) {

                                if (lo.get("goodSku").toString().equals(s.get("goodSku").toString())) {
                                    lost += Integer.parseInt(lo.get("shelefsum").toString());
                                }

                                if (Integer.parseInt(s.get("planQty").toString()) == lost) {
                                    cot++;
                                    if (cot == listData.size()) {
                                        Button btn = (Button) findViewById(R.id.button14);
                                        Drawable statu = getResources().getDrawable(R.drawable.button);
                                        btn.setBackgroundDrawable(statu);
                                        btn.setClickable(true);
                                    }

                                }
                            }
                        }


                        EditText editTexts = (EditText) findViewById(R.id.editText13);
                        editTexts.setText("");
                        EditText editTexts15 = (EditText) findViewById(R.id.editText15);
                        editTexts15.setText("");
                        editText15.setFocusable(true);
                        editText15.setFocusableInTouchMode(true);
                        editText15.requestFocus();

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

   public void backmeue(View v){
       Intent myIntent = new Intent();
       myIntent = new Intent(good_shelf.this, twolevel_menu.class);
       startActivity(myIntent);
       good_shelf.this.finish();

   }

}

