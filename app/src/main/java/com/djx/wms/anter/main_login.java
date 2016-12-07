package com.djx.wms.anter;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.tools.ConnTranPares;
import com.djx.wms.anter.tools.DyDictCache;
import com.djx.wms.anter.tools.TransactSQL;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import pers.lh.communication.TranCoreClass;

import static android.Manifest.permission.READ_CONTACTS;


public class main_login extends AppCompatActivity {
    private TextView textView;
    private TextView textView35;
    /* Activity handler */
    Handler handler = null;
    Message msg;
    private EditText username;
    private Intent intents;
    private Intent intent;/*创建一个新的intent对象*/
    private MsgReceiver msgReceiver;
    private boolean mIsExit;
    public static final String TAG = "login";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private Intent startIntent;
    private String ip = "";
    private Short port;

    private UpdateAppManager updateManager;
    private String Version = "";
    private String data = "";
    private String downUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代
        /*仓库下标初始化*/
        AppStart.GetInstance().waresubscript=0;

        Version = getAPPVersionCodeFromAPP(main_login.this);
        TranCoreClass tranCoreClass = new TranCoreClass();
        tranCoreClass.terminalType = 2;
        new Thread(networkTask).start();

        SharedPreferences sharedPreferencess = getSharedPreferences("serverconfig", 0);
        String name = sharedPreferencess.getString("username", "");
        String pass = sharedPreferencess.getString("pass", "");

        TextView textView148 = (TextView) findViewById(R.id.textView148);
        textView148.setText("version:"+getVersion());
        AppStart.GetInstance().version = getVersion();
        //记住用户密码
        if (name != "" && pass != "") {
            EditText username = (EditText) findViewById(R.id.user);
            EditText password = (EditText) findViewById(R.id.pass);
            username.setText(name);
            password.setText(pass);
        }
        username = (EditText) findViewById(R.id.user);
        CreateHandler();
        Log.d("loginActivity", "onCreate");

        intent = new Intent();/*创建一个新的intent对象*/
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("commnication.status");
        registerReceiver(msgReceiver, intentFilter);
        Log.d(TAG, "广播注册完成——————————————————————");

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView119 = (TextView) findViewById(R.id.textView119);
        Log.d(TAG, "状态----------------------------------------");
        Log.d(TAG, "" + AppStart.GetInstance().GetCommunicationConn() + "");

        if (AppStart.GetInstance().GetCommunicationConn()!= null) {
            textView2.setVisibility(View.VISIBLE);
            textView119.setVisibility(View.VISIBLE);
        }else {
            textView2.setVisibility(View.INVISIBLE);
            textView119.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
/*
    public static void main(){
        Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象

        t.start();
    }
*/

    @Override
    protected void onDestroy(){
        Log.d("main_lgin", "onDestroy.............");
        /*关闭的时候注意销毁handler*/
        unregisterReceiver(msgReceiver);
        ((AppStart) getApplication()).DelHandler(FinalManager.main_login);
        super.onDestroy();
    }

    //配置调转
    public void setconfigs(View v) {
        Intent intent = new Intent();/*创建一个新的intent对象*/
        intent.setClass(main_login.this, config.class);
        /*设置Intent的源地址和目标地址*/
        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
       /* main_login.this.finish();*/
    }

    public void LoginClickEvent(View v) {
        /*清空缓存字典*/
        DyDictCache.instance.RefDyDict("");

        EditText username = (EditText) findViewById(R.id.user);
        EditText password = (EditText) findViewById(R.id.pass);
        String name = username.getText().toString();
        String pass = password.getText().toString();
        name = TransactSQL.instance.filterSQL(name);
        pass = TransactSQL.instance.filterSQL(pass);

        if (name.equals("") || pass.equals("")) {
            android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(main_login.this);
            build.setMessage("用户名和密码不能为空！").show();
            com.github.ybq.android.spinkit.SpinKitView Landings = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
            Landings.setVisibility(View.INVISIBLE);
            return;
        }

        ip = AppStart.GetInstance().getIP();
        port = AppStart.GetInstance().getPort();

        if (ip.equals("") || port == 0) {
            android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(main_login.this);
            build.setMessage("请配置服务器！").show();
            com.github.ybq.android.spinkit.SpinKitView Landings = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
            Landings.setVisibility(View.INVISIBLE);
            return;
        }
        /*创建一个新的intent对象*/
/*

        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
*/

        //保存users
        AppStart.GetInstance().setUserconfig(name, pass);
        //获取传入user实体
        AppStart.GetInstance().initUserEntity();
        Log.d("login", "onLoginClickEvent.............");

        com.github.ybq.android.spinkit.SpinKitView Landing = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
        Landing.setVisibility(View.VISIBLE);

        Button loginbtn=(Button)findViewById(R.id.button3);
        loginbtn.setClickable(false);
        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
        loginbtn.setBackgroundDrawable(statusQuestionDrawable);

        /*启动通讯服务*/
        AppStart.GetInstance().loginpage = "main_login";
        startIntent = new Intent(this, AnterService.class);
        startService(startIntent);
        //获取链接是否成功点击登陆
        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            if (AppStart.GetInstance().GetCommunicationConn().Connector.getConnectState() == false) {
                Toast.makeText(getApplicationContext(), "网络异常请稍后再试！", Toast.LENGTH_SHORT).show();
                return;
            }
         /*   if(AppStart.GetInstance().GetCommunicationConn().state!=false){*/
            TranCoreClass tranCoreClass = null;
            tranCoreClass = (TranCoreClass) (AppStart.GetInstance().GetCommunicationConn().SyncSend(3, AppStart.GetInstance().GetUserEntity()));

            if (tranCoreClass != null) {
                if (tranCoreClass.getResult() == 0) {
                    UserEntity usere = ConnTranPares.GetData(tranCoreClass, UserEntity.class);
                    if (usere != null) {
                        usere.setUserID(usere.UserID);

                        AppStart.GetInstance().GetCommunicationConn().Connector.SetTid(tranCoreClass.getToken());
                        /*发送心跳*/
                        AppStart.GetInstance().GetCommunicationConn().Connector.BeginSendHeart();
                        usere.setUserPass(AppStart.GetInstance().GetUserEntity().getUserPass());
                        AppStart.GetInstance().SetUserEntity(usere);
                    }
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("status", String.valueOf(tranCoreClass.getResult()));
                bundle.putString("message", String.valueOf(tranCoreClass.getMessage()));
                message.setData(bundle);
                message.what = FinalManager.LoginResultTag;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login, message);


                Log.d(TAG, "LoginComplete");
                Log.d(TAG, tranCoreClass.getToken());
                Log.d(TAG, String.valueOf(tranCoreClass.getResult()));
            } else {
                Log.d(TAG, "login timeout");
            }
        }
    }

    /*广播接收器*/
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("login", "收到广播.............");
            int status = intent.getIntExtra("status", 0);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("status", String.valueOf(status));
            message.setData(bundle);

            if (bundle.get("status").equals("200") || bundle.get("status").equals("100")) {

                com.github.ybq.android.spinkit.SpinKitView Landings = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
                Landings.setVisibility(View.INVISIBLE);

                Button loginbtn=(Button)findViewById(R.id.button3);
                loginbtn.setClickable(true);
                Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
                loginbtn.setBackgroundDrawable(statusQuestionDrawable);
                message.arg1 = FinalManager.success;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login, message);
            }
            if (bundle.get("status").equals("629") || bundle.get("status").equals("102")) {

                Button loginbtn=(Button)findViewById(R.id.button3);
                loginbtn.setClickable(false);
                Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
                loginbtn.setBackgroundDrawable(statusQuestionDrawable);

                message.arg1 = FinalManager.ConnectFail;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login, message);
            }
        }
    }

    /*消息接收器*/
    private void CreateHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Log.d("login", msg.getData().toString());
                if(msg.arg1 == FinalManager.ConnectFail){
                    TextView B = (TextView) findViewById(R.id.textView119);
                    B.setText("等待重连");
                    B.setTextColor(Color.rgb(247, 9, 9));
                }
                if(msg.arg1 == FinalManager.success){

                    TextView B= (TextView) findViewById(R.id.textView119);
                    B.setText("正常");
                    B.setTextColor(Color.rgb(13, 235, 87));
                }
                if (msg.arg1 == FinalManager.ConnectFail) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");
                    Toast.makeText(getApplicationContext(), "登录失败，请检查网络和服务器配置", Toast.LENGTH_SHORT).show();
                }
                if (msg.what == FinalManager.LoginResultTag) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");
                    if (Short.parseShort(result) == 0) {
                        Toast.makeText(getApplicationContext(), (String) message, Toast.LENGTH_SHORT).show();
                        intent.setClass(main_login.this, whselelct.class);
                       /*设置Intent的源地址和目标地址*/
                        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
                        main_login.this.finish();
                    } else {
                        Toast.makeText(getApplicationContext(), (String) message, Toast.LENGTH_SHORT).show();
                        com.github.ybq.android.spinkit.SpinKitView Landing = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
                        Landing.setVisibility(View.INVISIBLE);
                    }
                }/*else if(msg.what == FinalManager.CommnicationBroadcast){
                    String result =  msg.getData().getString("status");
                    *//*textView35.setText(result);*//*
                }*/
                super.handleMessage(msg);
            }
        };
        ((AppStart) getApplication()).SetHandler(FinalManager.main_login, handler);

    }

    public void qrcodelogin(View v) {
        Intent myIntent = new Intent();
        myIntent = new Intent(main_login.this, qrcodelogin.class);
        startActivity(myIntent);
        main_login.this.finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            /*跳转home*/
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitApp() {
        Intent intent = new Intent();
        intent.setAction("net.loonggg.exitapp");
        this.sendBroadcast(intent);
    }

    public static String getAPPVersionCodeFromAPP(Context ctx) {
        int currentVersionCode = 0;
        String appVersionName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        return appVersionName;
    }
    Handler handlers = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            if (!val.equals("")) {
                String apkurl = val;
                updateManager = new UpdateAppManager(main_login.this);
                updateManager.checkUpdateInfo(apkurl);
            }
            // TODO
            // UI界面的更新等相关操作
        }
    };

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
//            data = HttpGetRequest.sendGet("http://wms.meitaomeitao.com/api/sys/aupdate?v=" + Version + "", "");
            data = HttpGetRequest.sendGet("http://http://192.168.10.254:8221//api/sys/aupdate?v="+Version+"", "");

            try {
                String[] stepOne = data.split(",");
                int c = stepOne.length;
                String[] downing = stepOne[3].split("\"");
                downUrl = downing[3];
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", downUrl);
                msg.setData(data);
                handlers.sendMessage(msg);
            } catch (Exception e) {
            }
        }
    };

}




