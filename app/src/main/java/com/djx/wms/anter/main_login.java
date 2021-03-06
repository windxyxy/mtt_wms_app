package com.djx.wms.anter;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.entity.DaoStatic;
import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.tools.ConnTranPares;
import com.djx.wms.anter.tools.DyDictCache;
import com.djx.wms.anter.tools.TransactSQL;

import pers.lh.communication.TranCoreClass;


public class main_login extends AppCompatActivity {
    private TextView textView;
    private TextView textView35;
    /* Activity handler */
    Handler handler = null;
    Message msg;
    private EditText username, password;
    private Intent intents;
    private Intent intent;/*创建一个新的intent对象*/
    private MsgReceiver msgReceiver;
    private boolean mIsExit;
    public static final String TAG = "login";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private Intent startIntent;
    private String ip = "";
    private short port;

    private UpdateAppManager updateManager;
    private String Version = "";
    private String data = "";
    private String downUrl = "";
    private String name;
    private String pass;
    private String nameReal;
    private String passReal;
    private Context mContext;
    private SharedPreferences sharedPreferencess;

    private UserEntity mEntity;
    private DaoStatic mStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代
        /*仓库下标初始化*/
        AppStart.GetInstance().waresubscript = 0;

        mContext = AppStart.getmContext();

        mEntity = new UserEntity();
        mEntity.setLoginState(-11);

        mStatic = new DaoStatic();
        mStatic.isIn = false;

        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);

        Version = getAPPVersionCodeFromAPP(main_login.this);
        TranCoreClass tranCoreClass = new TranCoreClass();
        tranCoreClass.terminalType = 2;
        new Thread(networkTask).start();

        sharedPreferencess = mContext.getSharedPreferences("serverconfig", 0);
        name = sharedPreferencess.getString("username", "");
        pass = sharedPreferencess.getString("pass", "");


        TextView textView148 = (TextView) findViewById(R.id.textView148);
        textView148.setText("version:" + getVersion());
        AppStart.GetInstance().version = getVersion();

        //记住用户密码
        if (username.getText().toString().equals("") && password.getText().toString().equals("")) {

            username.setText(name);
            password.setText(pass);

        }

        CreateHandler();


        intent = new Intent();/*创建一个新的intent对象*/
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("commnication.status");
        registerReceiver(msgReceiver, intentFilter);

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView119 = (TextView) findViewById(R.id.textView119);
        Log.e(TAG, "状态----------------------------------------");

        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            textView2.setVisibility(View.VISIBLE);
            textView119.setVisibility(View.VISIBLE);
        } else {
            textView2.setVisibility(View.INVISIBLE);
            textView119.setVisibility(View.INVISIBLE);
        }

    }


    //登錄按鈕
    public void LoginClickEvent(View v) {

        mStatic.isIn = true;

        /*清空缓存字典*/
        DyDictCache.instance.RefDyDict("");//-----------

        name = username.getText().toString();
        pass = password.getText().toString();

        nameReal = TransactSQL.instance.filterSQL(name);
        passReal = TransactSQL.instance.filterSQL(pass);

        if (nameReal.equals("") || passReal.equals("")) {
            android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(main_login.this);
            build.setMessage("用户名和密码不能为空！").show();
//            com.github.ybq.android.spinkit.SpinKitView Landings = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
//            Landings.setVisibility(View.INVISIBLE);
            return;
        }

        ip = AppStart.GetInstance().getIP();
        port = AppStart.GetInstance().getPort();

        Log.e("ip", "ip====" + ip);
        Log.e("port", "port===" + port);

        if (ip.equals("") || port == 0) {
            android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(main_login.this);
            build.setMessage("请配置服务器！").show();
//            com.github.ybq.android.spinkit.SpinKitView Landings = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
//            Landings.setVisibility(View.INVISIBLE);
            return;
        }
        //保存users
        AppStart.GetInstance().setUserconfig(nameReal, passReal);
        //获取传入user实体
        AppStart.GetInstance().initUserEntity();

//        Button loginbtn = (Button) findViewById(R.id.button3);
//        loginbtn.setClickable(false);
//        Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
//        loginbtn.setBackgroundDrawable(statusQuestionDrawable);

        /*启动通讯服务*/
        AppStart.GetInstance().loginpage = "main_login";
        startIntent = new Intent(this, AnterService.class);
        startService(startIntent);
        //获取链接是否成功点击登陆
        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            Log.e("GetCommunicationConn", "GetCommunicationConn==========");
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
                        //登录成功状态为1
                        usere.setLoginState(1);//--------
                        AppStart.GetInstance().setUserconfig(name, pass);

                        AppStart.GetInstance().GetCommunicationConn().Connector.SetTid(tranCoreClass.getToken());
                        Log.e("tranCoreClass.getToken()", "tranCoreClass.getToken()==" + tranCoreClass.getToken().toString());
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
            } else {
                Log.e(TAG, "login timeout");
            }
        }
    }

    /*广播接收器*/
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("commnication.status")) {
                Log.e("login", "收到广播.............");
                int status = intent.getIntExtra("status", 0);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("status", String.valueOf(status));
                message.setData(bundle);

                if (bundle.get("status").equals("200") || bundle.get("status").equals("100")) {
                    Log.e("login_status", "login_status=---200+100---=" + bundle.get("status"));

//                Button loginbtn = (Button) findViewById(R.id.button3);
//                loginbtn.setClickable(true);//自动点击登录按钮
//                loginbtn.setClickable(false);
//                Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.button);
//                loginbtn.setBackgroundDrawable(statusQuestionDrawable);
                    message.arg1 = FinalManager.success;
                    AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login, message);
                }
                if (bundle.get("status").equals("629") || bundle.get("status").equals("102")) {
                    Log.e("login_status", "login_status=---629+102---=" + bundle.get("status"));

//                Button loginbtn = (Button) findViewById(R.id.button3);
//                loginbtn.setClickable(false);
//                Drawable statusQuestionDrawable = getResources().getDrawable(R.drawable.disablebutton);
//                loginbtn.setBackgroundDrawable(statusQuestionDrawable);

                    message.arg1 = FinalManager.ConnectFail;
                    AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login, message);
                }
            }

        }
    }

    /*消息接收器*/
    private void CreateHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Log.e("login", "msg===" + msg.getData().toString());
                if (msg.arg1 == FinalManager.ConnectFail) {
                    Log.e("login_fail", "login_fail===" + msg.arg1);

                    TextView B = (TextView) findViewById(R.id.textView119);
                    B.setText("等待重连");
                    B.setTextColor(Color.rgb(247, 9, 9));

//                    Toast.makeText(getApplicationContext(), "登录失败，请检查网络和服务器配置", Toast.LENGTH_SHORT).show();
                }
                if (msg.arg1 == FinalManager.success) {
                    Log.e("login_success", "login_success===" + msg.arg1);

                    TextView B = (TextView) findViewById(R.id.textView119);
                    B.setText("正常");
                    B.setTextColor(Color.rgb(13, 235, 87));
                    return;//------
                }
//                if (msg.arg1 == FinalManager.ConnectFail) {
////                    String result = msg.getData().getString("status");
////                    String message = msg.getData().getString("message");
//                    Toast.makeText(getApplicationContext(), "登录失败，请检查网络和服务器配置", Toast.LENGTH_SHORT).show();
//                }
                if (msg.what == FinalManager.LoginResultTag) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");
//                    if (Short.parseShort(result) == 0 && mStatic.isLog() == true) {
                    if (Short.parseShort(result) == 0 && mStatic.isIn == true){
                        Log.e("login_loginTag", "login_loginTag===" + msg.what);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        intent.setClass(main_login.this, whselelct.class);
                       /*设置Intent的源地址和目标地址*/
                        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
                        main_login.this.finish();
                    } else {
//                        Log.e("message", "message===" + message);
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                /*else if(msg.what == FinalManager.CommnicationBroadcast){
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
            data = HttpGetRequest.sendGet("http://wms.meitaomeitao.com/api/sys/aupdate?v=" + Version + "", "");
//            data = HttpGetRequest.sendGet("http://http://192.168.10.254:8221//api/sys/aupdate?v="+Version+"", "");

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

    //配置服务器地址跳转
    public void setconfigs(View v) {
        Intent intent = new Intent();/*创建一个新的intent对象*/
        intent.setClass(main_login.this, config.class);
        /*设置Intent的源地址和目标地址*/
        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
    }

    /**
     * 获取版本号
     *
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("main_lgin", "onDestroy.............");
        /*关闭的时候注意销毁handler*/
        unregisterReceiver(msgReceiver);
        ((AppStart) getApplication()).DelHandler(FinalManager.main_login);
    }

}




