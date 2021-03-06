package com.djx.wms.anter;

/**
 * Created by lh on 2016/3/16.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.djx.wms.anter.entity.RoleInfor;
import com.djx.wms.anter.entity.UserEntity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AppStart extends Application {
    private static AppStart instance = null;
    private static Context mContext;
    private SharedPreferences sharedPreferencess;


    /*public static String appname="仓储管理系统";*/
    public static AppStart GetInstance() {
        return instance;
    }

    private String TAG = "Application";
    private Hashtable<String, Handler> handlerMap = new Hashtable<String, Handler>();

    private String IP = "";
    /* 反拣单单号记录*/
    public String counterorder = "";

    /* 反拣单单号记录*/
    public String lessorder = "";

    /*盘点记录保存*/
    public int pdsum = 0;
    public int cysum = 0;
    public String cyindex = "";

    /*仓内加工出库单号记录*/
    public String Jgorder = "";

    /*拣货单单号*/
    public String pickingCode = "";

    /*拣货单单号*/
    public int nextsum = 0;

    /*拣货数组*/
    public String[] pickstr;

    /*出库菜单出库单号*/
    public String outorder = "";

    /*仓库选择下标缓存*/
    public int waresubscript = 0;

    public String version = "";
    private short port = 0;
    public static String loginpage = "main_login";
    public static boolean switchs = true;
    public boolean mIsExit = true;
    public Thread serverthread;

    public Handler handlers = new Handler();

    public Runnable runnable;

    @Override
    public void onCreate() {

        Log.d(TAG, "Application onCreate");
        super.onCreate();

        mContext = getApplicationContext();

        instance = this;
        initUserEntity();
        CrashIni();


        initializationconfig();
    }


    public static Context getmContext() {
        return mContext;
    }

    public static int Warehouse;

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "Application onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "Application onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "Application onTrimMemory");
        super.onTrimMemory(level);
    }


    public void initializationconfig() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("serverconfig", 0);
        SharedPreferences.Editor editors = sharedPreferences.edit();
        if (getIP().equals("") && getPort() == 0) {
            setIP("101.200.81.221");
            setPort("9800");
        }
        editors.commit();
    }

    private void initServiceAddr() {
        SharedPreferences sharedPreferencess = mContext.getSharedPreferences(configKey, 0);
        String ip = sharedPreferencess.getString("ip", "");
        String p = sharedPreferencess.getString("port", "");
        this.IP = ip;
        this.port = (short) Integer.parseInt(p);
    }

    public String getIP() {

        String path = Environment.getExternalStorageDirectory().getPath();
        File imgFile = new File(path + "/wmsconfig");
        String filename = path + "/wmsconfig/config.txt";

        String arryconfig = "";
        arryconfig = readFileSdcardFile(filename);
        String[] data = new String[1];

        data = arryconfig.split(",");
        IP = data[0];
        return IP;
    }

    public void setIP(String IP) {


        Map<String, String> textviews = new HashMap<String, String>();
        textviews.put("IP", IP);
       /* savafile(textviews);*/

        FileWriter fw = null;
        BufferedWriter bw = null;
        String path = Environment.getExternalStorageDirectory().getPath();
        File imgFile = new File(path + "/wmsconfig");
        String filename = path + "/wmsconfig/config.txt";

        if (!imgFile.exists()) {
            imgFile.mkdir();
        }


        File dir = new File(filename);
        try {
            dir.createNewFile();
        } catch (Exception e) {
        }


        try {
            fw = new FileWriter(filename, true);//
            // 创建FileWriter对象，用来写入字符流
            /* bw = new BufferedWriter(fw); // 将缓冲对文件的输出*/

            bw = new BufferedWriter(fw); // 将缓冲对文件的输出
            String myreadline = textviews + "";
            bw.write(myreadline + "\n"); // 写入文件
            bw.newLine();
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();
        } catch (Exception e) {
        }
        this.IP = IP;
    }


    public void savafile(Map<String, String> textviews) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String path = Environment.getExternalStorageDirectory().getPath();
        File imgFile = new File(path + "/wmsconfig");
        String filename = path + "/wmsconfig/config.txt";


        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }


        File dir = new File(filename);
        try {
            dir.createNewFile();
        } catch
                (Exception e) {
        }


        try {
            fw = new FileWriter(filename, true);//
            // 创建FileWriter对象，用来写入字符流
           /* bw = new BufferedWriter(fw); // 将缓冲对文件的输出*/

            bw = new BufferedWriter(fw); // 将缓冲对文件的输出
            String myreadline = textviews + "";

            bw.write(myreadline + "\n"); // 写入文件
            bw.newLine();
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();
        } catch (Exception e) {

        }

    }


    public String readFileSdcardFile(String fileName) {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            String str = new String(buffer, "utf-8");

            String[] col = str.split("\\n");
            JSONObject a = new JSONObject(col[0]);
            JSONObject b = new JSONObject(col[2]);
            String c = b.get("port").toString();
            String arryconfig = "";

            arryconfig = a.get("IP").toString() + "," + c;
            return arryconfig;

         /*   fin.close();
            return arryconfig;*/


        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }

    }


    public short getPort() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File imgFile = new File(path + "/wmsconfig");
        String filename = path + "/wmsconfig/config.txt";

        String arryconfig = "";
        String[] data = new String[1];
        arryconfig = readFileSdcardFile(filename);
        data = arryconfig.split(",");

        try {
            port = (short) (Integer.parseInt(data[1]));
        } catch (Exception e) {

        }

        String[] arryconfigss = new String[2];

        return port;
    }

    public void setPort(String port) {

        Map<String, String> textviews = new HashMap<String, String>();
        textviews.put("port", port);
        savafile(textviews);

     /*   SharedPreferences sharedPreferences = getSharedPreferences("serverconfig", 0);
        SharedPreferences.Editor  editors  =  sharedPreferences.edit();
        editors.putString("port", port);
        editors.commit();
*/

        this.port = (short) Integer.parseInt(port);
    }


    public String initUserEntity() {
        sharedPreferencess = mContext.getSharedPreferences(configKey, 0);
        String name = sharedPreferencess.getString("username", "");
        String pass = sharedPreferencess.getString("pass", "");
        if (userEntity == null)
            userEntity = new UserEntity();
        userEntity.setLoginName(name);
        userEntity.setUserPass(pass);
        return userEntity.getUserName();
    }


    public void setUserconfig(String var1, String var2) {

        sharedPreferencess = mContext.getSharedPreferences(configKey, 0);
        SharedPreferences.Editor editors = sharedPreferencess.edit();
        editors.putString("username", var1);
        editors.putString("pass", var2);
        editors.commit();
    }


    private void IniServer() {
        // LocationServer.GetInstance();
    }

    private void CrashIni() {
        // CrashHandler catchHandler = CrashHandler.getInstance();
        // catchHandler.init(getApplicationContext());
    }

    private String configKey = "serverconfig";

    public String GetConfigKey() {
        return this.configKey;
    }

    private CommunicationConn conn = null;

    public void SetCommunication(CommunicationConn communicationConn) {
        this.conn = communicationConn;
    }

    public CommunicationConn GetCommunicationConn() {
        return conn;
    }


    private UserEntity userEntity = null;

    public UserEntity GetUserEntity() {
        return this.userEntity;
    }

    public void SetUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }


    public void HandlerSendMessage(String handlerName, Message msg) {
        Handler handler = handlerMap.get(handlerName);
        if (handler != null)
            handler.sendMessage(msg);
        else
            Log.d(TAG, "没有这个handler:" + handlerName);
    }

    public void SetHandler(String handlerName, Handler handler) {
        DelHandler(handlerName);
        handlerMap.put(handlerName, handler);
    }

    public void DelHandler(String handlerName) {
        handlerMap.remove(handlerName);
    }


    public int getUserID() {

        return userEntity.getUserID();
    }

    public String getWarehouseIDs() {

        return userEntity.WarehouseIDs();
    }

    public RoleInfor getRoleInfor() {

        return userEntity.getRoleInfor();
    }


}

