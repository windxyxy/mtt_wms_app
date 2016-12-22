package com.djx.wms.anter;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.tools.TransactSQL;

/**
 * Created by gfgh on 2016/3/14.
 */
public class buttom_state extends AppCompatActivity {

    //    private MsgReceiver msgReceiver;
    private BottomReceiver mReceiver;
    Handler handler = null;
    public static final String TAG = "CommunicationImpl";
    private AlertDialog p = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_state);
        /*初始化屏蔽键盘*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁用横屏
        /*初始化屏蔽头部*/
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();

//        msgReceiver = new MsgReceiver();
        mReceiver = new BottomReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("commnication.status.buttom");
        registerReceiver(mReceiver, intentFilter);
        Log.d(TAG, "广播注册完成——————————————————————");
        CreateHandler();


        acquireWakeLock();
        releaseWakeLock();

    }


    /* 广播接收器*/
    public class BottomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("loginActivity", "收到广播.............");

            int status = intent.getIntExtra("status", 0);
            Message message = new Message();


            Bundle bundle = new Bundle();
            bundle.putString("status", String.valueOf(status));
            message.setData(bundle);
            if (bundle.get("status").equals("629") || bundle.get("status").equals("102")) {
                message.arg1 = FinalManager.ConnectFail;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.buttom_state, message);
                AppStart.switchs = false;
                TextView B = (TextView) findViewById(R.id.state);
                B.setText("等待重连");
                B.setTextColor(Color.rgb(247, 9, 9));
                /*操作提示*/
                if (p == null) {
                    p = new AlertDialog.Builder(buttom_state.this)
                            .setTitle("提示")
                            .setMessage("等待连接")
                            .setCancelable(false)
                            .show();
                }
            }

            if (bundle.get("status").equals("200") || bundle.get("status").equals("100")) {
                message.arg1 = FinalManager.success;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.buttom_state, message);


                AppStart.switchs = true;
                TextView B = (TextView) findViewById(R.id.state);
                B.setText("正常");
                B.setTextColor(Color.rgb(13, 235, 87));
                 /*操作提示*/
                if (p != null) {
                    p.dismiss();
                    p = null;
                }
            }
        }

    }

    /* 消息接收器*/
    private void CreateHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 == FinalManager.ConnectFail) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");

                    AppStart.switchs = false;
                    TextView B = (TextView) findViewById(R.id.state);
                    B.setText("等待重连");
                    B.setTextColor(Color.rgb(247, 9, 9));


                    if (p == null) {
                        p = new AlertDialog.Builder(buttom_state.this)
                                .setTitle("提示")
                                .setMessage("等待连接")
                                .setCancelable(false)
                                .show();
                    }


                }

                if (msg.arg1 == FinalManager.success) {
                    AppStart.switchs = true;
                    TextView B = (TextView) findViewById(R.id.state);
                    B.setText("正常");
                    B.setTextColor(Color.rgb(13, 235, 87));
                    if (p != null) {
                        p.dismiss();
                        p = null;
                    }


                }

                super.handleMessage(msg);
            }
        };
        ((AppStart) getApplication()).SetHandler(FinalManager.buttom_state, handler);

    }

    /*页面销毁事件*/
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        ((AppStart) getApplication()).DelHandler(FinalManager.buttom_state);
        if (p != null) {
            p.dismiss();
        }
        super.onDestroy();
    }


    /* 页面加载完成*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            TextView A = (TextView) findViewById(R.id.Operator);
            String username = AppStart.GetInstance().initUserEntity();
            A.setText(username);
            if (AppStart.switchs == false) {
                TextView B = (TextView) findViewById(R.id.state);
                B.setText("等待重连");
                B.setTextColor(Color.rgb(247, 9, 9));
            } else {

                TextView B = (TextView) findViewById(R.id.state);
                B.setText("正常");
                B.setTextColor(Color.rgb(13, 235, 87));
            }
        }
    }


    /*全选事件*/
    public void selectall(EditText assembly) {
        assembly.setText(assembly.getText().toString());//添加这句后实现效果
        Spannable content = assembly.getText();
        Selection.selectAll(content);
    }

    /*获取焦点*/
    public void Getfocus(EditText assembly) {
        assembly.setFocusable(true);
        assembly.setFocusableInTouchMode(true);
        assembly.requestFocus();
    }

    /*非空验证*/
    public void Nonull(EditText assembly, String erro) {
        String content = TransactSQL.instance.filterSQL(assembly.getText().toString());
        if (content.equals("")) {
            assembly.setError(erro);
        }
    }

    /*超出滚动滚动*/
    public void roll(TextView assembly) {
        assembly.setMovementMethod(new ScrollingMovementMethod());
    }


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


    private PowerManager.WakeLock wakeLock = null;


    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }


}
