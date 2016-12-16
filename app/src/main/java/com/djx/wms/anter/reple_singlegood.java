package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/6/29.
 */
public class reple_singlegood extends buttom_state {


    private TextView mgoNo, wareCode, goodName, surplussum, Merchantcode;
    private EditText postion, goodCode, pickingsum;
    private List<Hashtable> listData = new ArrayList<Hashtable>();
    private int sums = 0, odd = 0;
    private Boolean blur = true, gridblur = true;


    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reple_singlegood);


        Merchantcode = (TextView) findViewById(R.id.editText57);
        goodName = (TextView) findViewById(R.id.editText58);
        goodCode = (EditText) findViewById(R.id.editText56);

        postion = (EditText) findViewById(R.id.editText45);
        surplussum = (TextView) findViewById(R.id.editText46);
        pickingsum = (EditText) findViewById(R.id.editText29);

        /*表格初始化*/
        gridview = (GridView) findViewById(R.id.gridView);

//        /*失去焦点触发事件*/
//        gridview.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    if (gridblur) {
//                        postion.setFocusable(true);
//                        postion.setFocusableInTouchMode(true);
//                        postion.requestFocus();
//                        postion.requestFocusFromTouch();
//                    } else {
//                        goodCode.setFocusable(true);
//                        goodCode.setFocusableInTouchMode(true);
//                        goodCode.requestFocus();
//                        goodCode.requestFocusFromTouch();
//                    }
//                }
//            }
//        });

        goodCode.setFocusable(true);
        goodCode.setFocusableInTouchMode(true);
        goodCode.requestFocus();
        goodCode.requestFocusFromTouch();


        /*货品回车事件*/
        goodCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums % 2 != 0) {
                        blur = false;
                        if (querygood()) {
                            selectall(goodCode);
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
        goodCode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querygood();
                }
            }
        });




          /*货位回车事件*/
        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    odd++;
                    if (odd % 2 != 0) {
                        if (querypos()) {
                            selectall(postion);
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
        postion.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querypos();
                }
            }
        });


        pickingsum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText editText29 = (EditText) findViewById(R.id.editText29);
                        if (!editText29.getText().toString().equals("")) {

                            int jsum = 0;
                            int bhsum = 0;
                            try {
                                jsum = Integer.parseInt(pickingsum.getText().toString());
                                bhsum = Integer.parseInt(surplussum.getText().toString());
                            } catch (Exception e) {
                            }

                            if (jsum > bhsum || jsum <= 0) {
                                pickingsum.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(reple_singlegood.this);
                                build.setMessage("上货数量不正确!").show();
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


    }


    /*货品查询货位*/
    public Boolean querygood() {

        String goodText = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if (goodText.equals("")) {
            return true;
        }
//        String SQL = "select top 1 a.*,b.remark as targetPos,dbo.getPositionId(b.whId,b.remark) " +
//                "targetId,d.stock,(select realStock from v_stock where positionID = dbo.getPositionId(b.whId,b.remark)" +
//                " and goodsID = a.goodsId) as realStock from t_movegoods_detail as a join " +
//                "t_movegoods as b on a.mgoId = b.indexId join t_movegoods_log as c on c.mgoNo = b.mgoNo " +
//                "join v_fillgoods as d on c.mgoNo = d.mgoNo and d.mgoType = 2 where b.mgoType = 2 and " +
//                "(b.orderStatus = 4 or b.orderStatus=5) and (a.planQty - a.realQty) > 0 and planQty != realQty and " +
//                "a.wareGoodsCodes = '" + goodText + "' and c.userID =" + AppStart.GetInstance().getUserID() + "";

        String SQL = "select top 1 a.*,(select inPosFullCode from t_movegoods_detail where inPosIndex != 0 and mgoNo = b.mgoNo) as targetPos," +
                "(select inPosIndex from t_movegoods_detail where inPosIndex != 0 and mgoNo = b.mgoNo) as targetId,d.stock," +
                "(select realStock from v_stock where positionID = (select inPosIndex from t_movegoods_detail where inPosIndex != 0 and mgoNo = b.mgoNo) " +
                "and goodsID = a.goodsId) as realStock from t_movegoods_detail as a " +
                "join t_movegoods as b on a.mgoId = b.indexId " +
                "join t_movegoods_log as c on c.mgoNo = b.mgoNo " +
                "join v_fillgoods as d on c.mgoNo = d.mgoNo and d.mgoType = 2 where b.mgoType = 2 and (b.orderStatus = 4 or b.orderStatus=5) and " +
                "(a.realOutQty - a.realInQty) > 0 and a.wareGoodsCodes = '"+goodText+"' and c.userID = "+AppStart.GetInstance().getUserID()+"";

        listData = Datarequest.GetDataArrayList(SQL);
        if (listData.size() != 0) {
            goodName.setText(listData.get(0).get("goodsName").toString());
            Merchantcode.setText(listData.get(0).get("goodsCode").toString());
            surplussum.setText(listData.get(0).get("stock").toString());


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
            gridblur = true;
            return false;
        } else {

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


            gridblur = false;
            goodCode.setError("货品条码不正确！");
            selectall(goodCode);
            return true;
        }

    }


    public Boolean querypos() {

        String bhposition = postion.getText().toString();
        bhposition = TransactSQL.instance.filterSQL(bhposition);
        if (bhposition.equals("")) {
            return true;
        }


        for (Map l : listData) {
            if (l.get("targetPos").toString().equals(bhposition)) {
                return false;
            }
        }

        postion.setError("货位信息不匹配！");
        return true;
    }


    public void sumbitgood(View v) {


        if (querypos()) {
            postion.setError("请输入正确的货位");
            return;
        }


        if (listData.size() == 0) {
            goodCode.setError("请输入正确的货品条码！");
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        Hashtable ParamValues = new Hashtable<>();
                        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_FILL");
                        ParamValues.put("msg", "output-varchar-8000");

                        ParamValues.put("mgotype", "2");
                        ParamValues.put("mgoNo", listData.get(0).get("mgoNo").toString());


                        String wareGoodsCodes = TransactSQL.instance.filterSQL(goodCode.getText().toString());
                        ParamValues.put("wareGoodsCodes", wareGoodsCodes);


                        ParamValues.put("realStock", pickingsum.getText().toString());

                        int userId = AppStart.GetInstance().getUserID();
                        ParamValues.put("createId", "" + userId + "");

                        ParamValues.put("whid", AppStart.GetInstance().Warehouse);

                        String inPosFullCode = TransactSQL.instance.filterSQL(postion.getText().toString());
                        ParamValues.put("inPosFullCode", inPosFullCode);


                        List<Hashtable> data = new ArrayList<Hashtable>();
                        data = Datarequest.GETstored(ParamValues);
                        if (data.get(0).get("result").toString().equals("0.0")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(reple_singlegood.this);
                            build.setMessage("补货成功！").show();

                            Intent intent = new Intent();
                            intent.setClass(reple_singlegood.this, reple_singlegood.class);
                            startActivity(intent);/*调用startActivity方法发送意图给系统*/
                            reple_singlegood.this.finish();
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(reple_singlegood.this);
                            build.setMessage(data.get(0).get("msg").toString()).show();
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


    /*表格刷新*/
    private void gridRefresh() {
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

    }

    /*表格头部*/
    public void addHeader() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText", "拣货区货位");
        map.put("ItemTexts", "库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格数据绑定*/
    public void addData() {
        try {
            for (Map i : listData) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemText", i.get("targetPos").toString());
                map.put("ItemTexts", i.get("realStock").toString());
                srcTable.add(map);
            }
        } catch (Exception e) {
        }

        saTable.notifyDataSetChanged(); //更新数据
    }

    /*表格列宽固定*/
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }


    public void singlebacks(View v) {
        Intent intent = new Intent();
        intent.setClass(reple_singlegood.this, reple_task.class);
        startActivity(intent);
        reple_singlegood.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(reple_singlegood.this, reple_task.class);
            startActivity(myIntent);
            reple_singlegood.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
