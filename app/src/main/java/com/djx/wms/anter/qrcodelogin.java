package com.djx.wms.anter;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.tools.ConnTranPares;
import com.djx.wms.anter.tools.TransactSQL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import pers.lh.communication.TranCoreClass;

/**
 * Created by gfgh on 2016/4/7.
 */
public class qrcodelogin extends AppCompatActivity {


    private Intent intent;/*创建一个新的intent对象*/

    private MsgReceiver msgReceiver;
    public static final String TAG = "prclogin";
    private Handler handler = null;
    private boolean mIsExit;
    private int TIME = 100;
    private String Version = "";
    private UpdateAppManager updateManager;
    private List<Hashtable> listData = new ArrayList<Hashtable>();

    private String data = "";
    private String downUrl = "";
    private UserEntity mEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcodelogin);

        //开机去除屏幕锁
        KeyguardManager kManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = kManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();


        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();
        /*仓库下标初始化*/
        AppStart.GetInstance().waresubscript = 0;

        mEntity = new UserEntity();
        mEntity.setLoginState(-11);//未登录状态


        Version = getAPPVersionCodeFromAPP(qrcodelogin.this);

        TranCoreClass tranCoreClass = new TranCoreClass();
        tranCoreClass.terminalType = 2;

        new Thread(networkTask).start();


        TextView textView148 = (TextView) findViewById(R.id.textView149);
        textView148.setText("version:" + getVersion());
        AppStart.GetInstance().version = getVersion();

        AppStart.GetInstance().runnable = new Runnable() {
            @Override
            public void run() {
                // handler自带方法实现定时器
                try {
                    AppStart.GetInstance().handlers.postDelayed(this, TIME);
                    if (AppStart.GetInstance().GetCommunicationConn() != null) {

                        if (!AppStart.GetInstance().GetCommunicationConn().Connector.GetTid().equals("")) {
                            Intent intent = new Intent();
                            intent.setClass(qrcodelogin.this, whselelct.class);
                            startActivity(intent);
                            AppStart.GetInstance().handlers.removeCallbacks(AppStart.GetInstance().runnable);
                            qrcodelogin.this.finish();
                        }
                    }
                 /*   System.out.println("do...");*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("exception...");
                }
            }


        };

        /* 循环判断线程*/
        AppStart.GetInstance().handlers.postDelayed(AppStart.GetInstance().runnable, TIME);
        CreateHandler();
        Log.d("loginActivity", "onCreate");
        intent = new Intent();/*创建一个新的intent对象*/

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("commnication.status");
        registerReceiver(msgReceiver, intentFilter);
        Log.d(TAG, "广播注册完成——————————————————————");

        EditText loginform = (EditText) findViewById(R.id.editText24);
        loginform.setFocusable(true);
        loginform.setFocusableInTouchMode(true);
        loginform.requestFocus();
        loginform.requestFocusFromTouch();

        /*回车事件复写*/
        loginform.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    userlogin();
                    return false;
                }
                return false;
            }
        });

    }


    public void userlogin() {
        EditText loginform = (EditText) findViewById(R.id.editText24);
        String userlogin = loginform.getText().toString();
        String arry[] = new String[1];
        arry = userlogin.split("\\^");
        loginform.setText("");
        if (arry.length != 2) {
            Toast.makeText(getApplicationContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
            loginform.setText("");
            return;
        }

        String name = arry[0];
        String pass = arry[1];

        name = TransactSQL.instance.filterSQL(name);
        pass = TransactSQL.instance.filterSQL(pass);

        AppStart.GetInstance().setUserconfig(name, pass);
        //获取传入user实体
        AppStart.GetInstance().initUserEntity();
        Log.d("qrcodelogin", "onLoginClickEvent.............");



        /*启动通讯服务*/
        AppStart.GetInstance().loginpage = "qrcodelogin";
        Intent startIntent = new Intent(this, AnterService.class);
        startService(startIntent);


        //获取链接是否成功点击登陆
        if (AppStart.GetInstance().GetCommunicationConn() != null) {


            AppStart.GetInstance().GetCommunicationConn().Connector.SetVersion(getVersion());

            if (AppStart.GetInstance().GetCommunicationConn().Connector.getConnectState() == false) {
                Toast.makeText(getApplicationContext(), "网络异常请稍后再试！", Toast.LENGTH_SHORT).show();
                return;
            }

            TranCoreClass tranCoreClass = null;
            tranCoreClass = (TranCoreClass) (AppStart.GetInstance().GetCommunicationConn().SyncSend(3, AppStart.GetInstance().GetUserEntity()));
            if (tranCoreClass != null) {
                if (tranCoreClass.getResult() == 0) {
                    UserEntity usere = ConnTranPares.GetData(tranCoreClass, UserEntity.class);
                    if (usere != null) {
                        usere.setUserID(usere.UserID);

                        usere.setLoginState(1);//-----

                        AppStart.GetInstance().handlers.removeCallbacks(AppStart.GetInstance().runnable);
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

                AppStart.GetInstance().HandlerSendMessage(FinalManager.qrcdelogin, message);


                Log.d(TAG, "LoginComplete");
                Log.d(TAG, tranCoreClass.getToken());
                Log.d(TAG, String.valueOf(tranCoreClass.getResult()));
            } else {
                Log.d(TAG, "login timeout");
            }

        }

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
            int versionCode = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(msgReceiver);
        ((AppStart) getApplication()).DelHandler(FinalManager.qrcdelogin);
        AppStart.GetInstance().handlers.removeCallbacks(AppStart.GetInstance().runnable);
        super.onDestroy();
    }

    /*广播接收器*/
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("loginActivity", "收到广播.............");
            int status = intent.getIntExtra("status", 0);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("status", String.valueOf(status));
            message.setData(bundle);

            /*if(bundle.get("status").equals("200") || bundle.get("status").equals("100")){
                message.what= FinalManager.CommnicationBroadcast;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.qrcdelogin, message);
            }*/


            if (bundle.get("status").equals("629") || bundle.get("status").equals("102")) {
                message.arg1 = FinalManager.ConnectFail;
                AppStart.GetInstance().HandlerSendMessage(FinalManager.qrcdelogin, message);
            }

        }
    }

    /*消息接收器*/
    private void CreateHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 == FinalManager.ConnectFail) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");
                    Toast.makeText(getApplicationContext(), "登录失败，请检查网络和服务器配置", Toast.LENGTH_SHORT).show();
                }
                if (msg.what == FinalManager.LoginResultTag) {
                    String result = msg.getData().getString("status");
                    String message = msg.getData().getString("message");


                    if (Short.parseShort(result) == 0) {
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                        Log.e("message","qr__message=="+message);

                        intent.setClass(qrcodelogin.this, whselelct.class);
                       /*设置Intent的源地址和目标地址*/
                        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
                        qrcodelogin.this.finish();
                    } else {
                        Log.d("登录信息", "----------------------------");
                        Log.d("登录信息", message);
                        Toast.makeText(getApplicationContext(), (String) message, Toast.LENGTH_SHORT).show();
                    }


                } else if (msg.what == FinalManager.CommnicationBroadcast) {
                    String result = msg.getData().getString("status");
                    /*textView35.setText(result);*/
                }
                super.handleMessage(msg);
            }
        };
        ((AppStart) getApplication()).SetHandler(FinalManager.qrcdelogin, handler);

    }

    public void backlogin(View v) {
        Intent intent = new Intent();
        intent.setClass(qrcodelogin.this, main_login.class);
         /*设置Intent的源地址和目标地址*/
        startActivity(intent);       /*调用startActivity方法发送意图给系统*/
        qrcodelogin.this.finish();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
                 /*跳转到桌面*/
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);

            return true;

        }

        return super.onKeyDown(keyCode, event);
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
                updateManager = new UpdateAppManager(qrcodelogin.this);
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
//            data = HttpGetRequest.sendGet("http://192.168.10.254:8221//api/sys/aupdate?v="+Version+"", "");

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
