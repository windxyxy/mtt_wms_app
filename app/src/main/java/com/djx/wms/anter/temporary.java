package com.djx.wms.anter;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.djx.wms.anter.entity.SPEntity;
import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/4/7.
 */
public class temporary extends buttom_state {
    private TextView textView26, tv_cusIdCon, edit26;
    private EditText editText28, editText27, editText25;
    private int sums = 0, realStock = 0;
    private List<Hashtable> listhas = new ArrayList<Hashtable>();

    private String nextsku = "";
    int sum = 0;

    private Boolean statce = false;
    private String LocationID = "";

    private List<Hashtable> listData = new ArrayList<Hashtable>();
    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary);
        gridview = (GridView) findViewById(R.id.gv_gridview);
        textView26 = (TextView) findViewById(R.id.editText26);
        tv_cusIdCon = (TextView) findViewById(R.id.tv_cusIdContent);
        editText28 = (EditText) findViewById(R.id.editText28);
        editText27 = (EditText) findViewById(R.id.editText27);
        editText25 = (EditText) findViewById(R.id.editText25);

        edit26 = (TextView) findViewById(R.id.editText26);
        roll(edit26);


        /*获取焦点*/
        editText27.setFocusable(true);
        editText27.setFocusableInTouchMode(true);
        editText27.requestFocus();


        //输入框值change事件
        editText25.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        LocationID = "";
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });


        editText25.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
                    Hashtable ParamValue = new Hashtable<>();
                    ParamValue.put("SPName", "PRO_UP_GOODS_CHECK_POSITION");
                    String username = AppStart.GetInstance().initUserEntity();
                    ParamValue.put("whid", AppStart.GetInstance().Warehouse);
                    ParamValue.put("PosFullCode", Text25);
                    ParamValue.put("type", 0);
                    ParamValue.put("msg", "output-varchar-100");
                    ParamValue.put("msg1", "output-varchar-100");

                    List<Hashtable> result = new ArrayList<Hashtable>();
                    result = Datarequest.GETstored(ParamValue);

                    if (result.get(0).get("result").toString().equals("0.0")) {
                        LocationID = result.get(0).get("msg").toString();
                    } else {
                        editText25.setError(result.get(0).get("msg").toString());
                        editText25.setText(editText25.getText().toString());//添加这句后实现效果
                        Spannable content = editText25.getText();
                        Selection.selectAll(content);
                        return true;
                    }


                    return false;
                }
                return false;
            }
        });




        /*失去焦点触发事件*/
        editText25.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
                    Hashtable ParamValue = new Hashtable<>();
                    ParamValue.put("SPName", "PRO_UP_GOODS_CHECK_POSITION");
                    String username = AppStart.GetInstance().initUserEntity();
                    ParamValue.put("whid", AppStart.GetInstance().Warehouse);
                    ParamValue.put("PosFullCode", Text25);
                    ParamValue.put("type", 0);
                    ParamValue.put("msg", "output-varchar-100");
                    ParamValue.put("msg1", "output-varchar-100");


                    List<Hashtable> result = new ArrayList<Hashtable>();
                    result = Datarequest.GETstored(ParamValue);
                    if (result.get(0).get("result").toString().equals("0.0")) {
                        LocationID = result.get(0).get("msg").toString();
                    } else {
                        editText25.setError(result.get(0).get("msg").toString());
                        editText25.setText(editText25.getText().toString());//添加这句后实现效果
                        Spannable content = editText25.getText();
                        Selection.selectAll(content);
                    }

                }
            }
        });

        /*失去焦点触发*/
        editText27.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    enters(v);
                }
            }
        });

           /*回车事件复写*/
        editText27.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    enters(v);
                    return true;
                }
                return true;
            }
        });
    }


    public void enters(View v) {

        sum++;
        if (sum % 2 != 0) {
            editText27.setText(editText27.getText().toString());//添加这句后实现效果
            if (quergood()) {
                return;
            }
            if (!editText27.getText().toString().equals("")) {
                nextsku = editText27.getText().toString();
            }
        }
    }



    public Boolean quergood() {
        Boolean open = true;
        String sku = editText27.getText().toString();
        sku = TransactSQL.instance.filterSQL(sku);
        if (sku.equals("")) {
            editText27.setError("请输入正确的货品！");
            return true;
        }
        if (!nextsku.equals("")) {
            if (statce) {
                if (nextsku.equals(sku)) {
                    int sum = Integer.parseInt(editText28.getText().toString());
                    sum = sum + 1;
                    editText28.setText("" + sum + "");
                    open = false;
                    return false;
                }
            }
        }

//        String SQL = "select * from  v_querygoods_android  where  wareGoodsCodes='" + sku + "' and warehouseId=" + AppStart.GetInstance().Warehouse + " ";
        String SQL = " select * from  v_querygoods_android as a,t_goods as b  where a.indexId = b.indexId and a.wareGoodsCodes ='" + sku + "' and warehouseId=" + AppStart.GetInstance().Warehouse + "";

        listhas = Datarequest.GetDataArrayList(SQL);
        if (listhas.size() != 0) {
            tv_cusIdCon.setText(listhas.get(0).get("goodsCode").toString());
            textView26.setText(listhas.get(0).get("goodsName").toString());
            editText28.setText("1");


            String SQL1 = "select * from v_stock where wareGoodsCodes='" + sku + "'and (whAareType='BH' or whAareType='JH') and warehouseId=" + AppStart.GetInstance().Warehouse + "";
            listData = new ArrayList<>();
            listData = Datarequest.GetDataArrayList(SQL1);
            if (listData.size() != 0) {
            /*如果没有数据刷新表格重新加载*/
                srcTable = new ArrayList<HashMap<String, String>>();
                saTable = new SimpleAdapter(this,
                        srcTable,// 数据来源
                        R.layout.gridtext,//XML实现
                        new String[]{"ItemText", "ItemTexts"},  // 动态数组与ImageItem对应的子项
                        new int[]{R.id.ItemText, R.id.ItemTexts});
                // 添加并且显示
                gridview.setAdapter(saTable);
                //添加表头
                addHeader();
                //添加数据测试
                addData();
                setListViewHeightBasedOnChildren(gridview);

            } else {/*AlertDialog.Builder build = new AlertDialog.Builder(out_picking.this);
            build.setMessage("库存不足！").show();*/
            }

            statce = true;
            return false;
        } else {
            EditText edit27 = (EditText) findViewById(R.id.editText27);
            edit27.setError("商品信息不存在，请同步商品信息！");
            TextView text = (TextView) findViewById(R.id.editText26);
            text.setText("");
            statce = false;

            return true;
        }


    }

    /*表格头部*/
    public void addHeader() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText", "货位编码");
        map.put("ItemTexts", "库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格数据绑定*/
    public void addData() {
        for (Map i : listData) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText", i.get("posCode").toString());
            map.put("ItemTexts", i.get("realStock").toString());
            srcTable.add(map);
        }
        saTable.notifyDataSetChanged(); //更新数据
    }


    public void purchase_back(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(temporary.this, exception_menu.class);
        startActivity(myIntent);
        temporary.this.finish();
    }

    public void savagood(View v) {

        String Text26 = edit26.getText().toString();

        if (Text26.equals("")) {
            AlertDialog.Builder build = new AlertDialog.Builder(temporary.this);
            build.setMessage("请查询货品！").show();
            return;
        }

        String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
        if (editText25.getText().toString().equals("")) {
            editText25.setError("请输入货位！");
            return;
        }
        String Text27 = TransactSQL.instance.filterSQL(editText27.getText().toString());

        if (Text27.equals("")) {
            editText27.setError("请输入货品！");
            return;
        }

        String Textsum = editText28.getText().toString();
        if (Textsum.equals("") || Textsum.equals("0")) {
            editText28.setError("数量不能为空或0！");
            return;
        }


        String sku = editText27.getText().toString();
        sku = TransactSQL.instance.filterSQL(sku);
        String SQL = "select *from   v_querygoods_android  where  wareGoodsCodes='" + sku + "' and warehouseId=" + AppStart.GetInstance().Warehouse + " ";
        listhas = Datarequest.GetDataArrayList(SQL);
        if (listhas.size() != 0) {
        } else {
            editText27.setError(" 货品不存在！");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将上架该商品！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String Text25 = TransactSQL.instance.filterSQL(editText25.getText().toString());
                        String Text27 = TransactSQL.instance.filterSQL(editText27.getText().toString());
                        String Textsum = editText28.getText().toString();

                        Hashtable ParamValues = new Hashtable<>();
                        ParamValues.put("SPName", "PRO_UP_GOODS_ADD");
                        ParamValues.put("wareGoodsCodes", Text27);
                        ParamValues.put("msg", "output-varchar-8000");
                        ParamValues.put("PosFullCode", Text25);
                        ParamValues.put("warehouseId", AppStart.GetInstance().Warehouse);
                        ParamValues.put("planQty", Textsum);

                        List<Hashtable> results = new ArrayList<Hashtable>();
                        results = Datarequest.GETstored(ParamValues);

                        if (results.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder builds = new AlertDialog.Builder(temporary.this);
                            builds.setMessage("上架成功").show();

                            Intent myIntent = new Intent();
                            myIntent = new Intent(temporary.this, temporary.class);
                            startActivity(myIntent);
                            temporary.this.finish();
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(temporary.this);
                            build.setMessage(results.get(0).get("msg").toString()).show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(temporary.this, exception_menu.class);
            startActivity(myIntent);
            temporary.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
