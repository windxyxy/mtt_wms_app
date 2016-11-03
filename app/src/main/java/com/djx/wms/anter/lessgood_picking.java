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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.djx.wms.anter.tools.Datarequest;
import com.djx.wms.anter.tools.TransactSQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by gfgh on 2016/5/23.
 */
public class lessgood_picking extends  buttom_state {



    /*private List<Map<String, String>> gridData= new ArrayList<Map<String, String>>();*/
    /*private List<Map<String, String>> warelist= new ArrayList<Map<String, String>>();*/
    private List<Hashtable> gridData = new ArrayList<Hashtable>();
    private List<Hashtable> warelist = new ArrayList<Hashtable>();
    private TextView mgoNo,wareCode,goodName,surplussum;
    private EditText postion,goodCode,pickingsum;


    public GridView gridview;
    protected ArrayList<HashMap<String, String>> srcTable;
    protected SimpleAdapter saTable;// 适配器


    private int sums=0,sum=0,realStock=0;
    private String replenorder="",nextsku="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessgood_picking);

        String SQL ="select *from  v_error_little  where mgoType=5 and mgoNo='"+AppStart.GetInstance().lessorder+"' and whId="+AppStart.GetInstance().Warehouse+"";
        warelist= Datarequest.GetDataArrayList(SQL);

        if(warelist.size()!=0){
            String SQLS = "select * from v_stock  where  wareGoodsCodes='" + warelist.get(0).get("wareGoodsCodes").toString() + "' and warehouseId='" + AppStart.GetInstance().Warehouse + "' and whAareType='JH'  and realStock!=0";
            gridData = Datarequest.GetDataArrayList(SQLS);
            if (gridData.size()== 0) {
                AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
                build.setMessage("拣货区下没有该库存！").show();
                Intent intent = new Intent();
                intent.setClass(lessgood_picking.this, less_goods.class);
                startActivity(intent);
                lessgood_picking.this.finish();
            }
        }else{
            AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
            build.setMessage("该订单已拣货完成！").show();
            Intent intent = new Intent();
            intent.setClass(lessgood_picking.this, less_goods.class);
            startActivity(intent);
            lessgood_picking.this.finish();
            return;
        }












      /*  Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        replenorder=intent.getStringExtra("order");
        gridData= (List) bundle.getSerializable("griddata");
        warelist= (List) bundle.getSerializable("wareCode");
*/
        /* 获取组件*/
        mgoNo=(TextView)findViewById(R.id.editText56);
        wareCode=(TextView)findViewById(R.id.editText57);
        goodName=(TextView)findViewById(R.id.editText58);
        surplussum=(TextView)findViewById(R.id.textView132);

        postion=(EditText)findViewById(R.id.editText45);
        goodCode=(EditText)findViewById(R.id.editText46);
        pickingsum=(EditText)findViewById(R.id.editText29);

        /*货品名称滚动*/
        roll(goodName);

        mgoNo.setText(warelist.get(0).get("mgoNo").toString());
        wareCode.setText(warelist.get(0).get("wareGoodsCodes").toString());
        goodName.setText(warelist.get(0).get("goodsName").toString());
        surplussum.setText(warelist.get(0).get("stock").toString());


        postion.setFocusable(true);
        postion.setFocusableInTouchMode(true);
        postion.requestFocus();




        //表格初始化
        gridview = (GridView) findViewById(R.id.gridView);
        srcTable = new ArrayList<HashMap<String, String>>();
        saTable = new SimpleAdapter(this,
                srcTable,// 数据来源
                R.layout.gridtext,//XML实现
                new String[] {"ItemText","ItemTexts"},  // 动态数组与ImageItem对应的子项
                new int[] { R.id.ItemText,R.id.ItemTexts});
        // 添加并且显示
        gridview.setAdapter(saTable);
        //添加表头
        addHeader();

        //添加数据测试
        addData();
        setListViewHeightBasedOnChildren(gridview);

        //输入框值change事件
        pickingsum.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {

                        if (!pickingsum.getText().toString().equals("")) {
                            int jhsum=0;
                            try{
                                 jhsum = Integer.parseInt(pickingsum.getText().toString());
                            }catch (Exception e){}

                            int demand = Integer.parseInt(surplussum.getText().toString());
                            if (jhsum > realStock || jhsum <= 0 || jhsum>demand) {
                                pickingsum.setText("");
                                AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
                                build.setMessage("拣货数量不正确！").show();

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






        /*货位回车事件*/
        postion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sums++;
                    if (sums % 2 != 0) {
                        if (querypos()) {
                            selectall(postion);
                            return true;
                        }else {
                            return false;
                        }

                    }

                }
                return true;
            }
        });



       /*货品条码回车事件*/
        goodCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sum++;
                    if (sum % 2 != 0) {
                        querycode();
                    }
                    return true;
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

         /*失去焦点触发事件*/
        goodCode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    querycode();
                }
            }
        });

    }

    /*查询货位*/
    public Boolean querypos(){

            goodCode.setText("");
            pickingsum.setText("");
            nextsku="";
            String bhposition = TransactSQL.instance.filterSQL(postion.getText().toString());

            if(bhposition.equals("")){
            return false;
            }

            for (Map l : gridData) {
                if (l.get("posCode").toString().equals(bhposition)) {
                    realStock=Integer.parseInt(l.get("realStock").toString());
                    return false;
                }
            }

            postion.setError("货位信息不匹配！");
            return true;

    }

    /*查询货品*/
    public Boolean querycode(){

            String values = TransactSQL.instance.filterSQL(goodCode.getText().toString());

            /*为空判断*/
            if(values.equals("")){
               return true;
            }


            if (!values.equals(wareCode.getText().toString())) {
                nextsku = goodCode.getText().toString();
                goodCode.setError("货品条码不正确！");
                selectall(goodCode);
            }else {
                selectall(goodCode);
                repeat();
                nextsku = goodCode.getText().toString();
            }


        return true;
    }


    public void repeat(){

        if(nextsku.equals(goodCode.getText().toString())){
            int sursum=0;
            try {sursum=Integer.parseInt(pickingsum.getText().toString());}
            catch (Exception e){}
            sursum++;

            if(realStock==0){
                AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
                build.setMessage("请扫入库存大于0的货位编码！").show();
                return;
            }

            int surplue=Integer.parseInt(surplussum.getText().toString());
            if(sursum<=realStock && sursum<=surplue){
                pickingsum.setText(""+sursum+"");
            }else {
                AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
                build.setMessage("拣货数量不正确！").show();
            }

        }
        else {
            pickingsum.setText("1");
        }

    }







    public void lesssava(View v){


        String  postiontext = TransactSQL.instance.filterSQL(postion.getText().toString());
        if(postiontext.equals("")){
            postion.setError("请输入正确的货位！");
            return;
        }


        String goodCodetext = TransactSQL.instance.filterSQL(goodCode.getText().toString());
        if (!goodCodetext.equals(wareCode.getText().toString())) {
            goodCode.setError("货品不匹配！");
            return;
        }


        if(pickingsum.getText().toString().equals("")) {
            pickingsum.setError("请输入拣货数量！");
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你将保存该信息！")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        sumbit();
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

    /*完成提交*/
    public void sumbit(){
        Hashtable ParamValues =new Hashtable<>();
        ParamValues.put("SPName", "PRO_MOVESGOODS_HANDHELD_ADD");
        ParamValues.put("msg", "output-varchar-8000");
        ParamValues.put("mgotype", "5");
        ParamValues.put("mgoNo", AppStart.GetInstance().lessorder);


        String   wareGoodsCodes=TransactSQL.instance.filterSQL(goodCode.getText().toString());
        ParamValues.put("wareGoodsCodes",wareGoodsCodes);


        String   realStock=TransactSQL.instance.filterSQL(pickingsum.getText().toString());
        ParamValues.put("realStock",realStock);

        int  userId=AppStart.GetInstance().getUserID();
        ParamValues.put("createId",""+userId+"");
        ParamValues.put("whid", AppStart.GetInstance().Warehouse);


        String   posFullCode=TransactSQL.instance.filterSQL(postion.getText().toString());
        ParamValues.put("posFullCode",posFullCode);
        ParamValues.put("inPosFullCode","");



        List<Hashtable> data = new ArrayList<Hashtable>();
        data = Datarequest.GETstored(ParamValues);
        if(data.get(0).get("result").toString().equals("0.0")){
            AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
            build.setMessage("拣货成功！").show();
            Intent intent = new Intent();
            intent.setClass(lessgood_picking.this, lessgood_picking.class);
            startActivity(intent);/*调用startActivity方法发送意图给系统*/
            lessgood_picking.this.finish();
        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(lessgood_picking.this);
            build.setMessage(data.get(0).get("msg").toString()).show();
        }


    }




    /*表格头部*/
    public void addHeader(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemText","货位");
        map.put("ItemTexts","库存数量");
        srcTable.add(map);
        saTable.notifyDataSetChanged(); //更新数据
    }
    /*表格数据绑定*/
    public void addData(){
        for(Map i:gridData){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemText",i.get("posCode").toString());
            map.put("ItemTexts",i.get("realStock").toString());
            /*if(i.get("realStock").toString().equals("")){
                sumstatce=true;
                Stocking=i.get("posCode").toString();
            }*/
            srcTable.add(map);
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








    /*返回*/
    public void backless_goods(View v){
        Intent myIntent = new Intent();
        myIntent = new Intent(lessgood_picking.this, less_goods.class);
        startActivity(myIntent);
        lessgood_picking.this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent myIntent = new Intent();
            myIntent = new Intent(lessgood_picking.this, less_goods.class);
            startActivity(myIntent);
            lessgood_picking.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }






}
