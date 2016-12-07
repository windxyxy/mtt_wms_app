package com.djx.wms.anter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.djx.wms.anter.ReceviePro.IOrderTran;
import com.djx.wms.anter.ReceviePro.MttNotification;
import com.djx.wms.anter.ReceviePro.OrderProThread;
import com.djx.wms.anter.ReceviePro.OrderRegister;

import java.util.Hashtable;

import pers.lh.communication.PostClass;


public class AnterService extends Service {


    public CommunicationConn conn;
    // private Intent intent = new Intent("com.djx.wms.anter.RECEIVER");

    public static Service GetServiceInstans() {
        return AnterService.serviceInstans;
    }

    public static Service serviceInstans = null;

    public AnterService() {
    }

    public static final String TAG = "AnterService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();



        /*
        * 消息推送
        * */

        try {
            Thread.sleep(300);
            MttNotification mttNotification = new MttNotification();
            Log.e("anter","消息推送");
            OrderRegister.GetInstance().Set("209", mttNotification);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e("Message"," message 处理");
        /* message 处理 */
        OrderProThread.GetInstance().BeginThread();

        AppStart.GetInstance().serverthread = new Thread() {
            @Override
            public void run() {
                //intent.putExtra("progress", 1);
                // sendBroadcast(intent);
                    /* 初始化通信服务 */
                AppStart appConfig = (AppStart) getApplication();
                String ip = AppStart.GetInstance().getIP();
                short port = AppStart.GetInstance().getPort();

                Log.d(TAG, "ip:" + ip);
                Log.d(TAG, "port:" + port);

                conn = new CommunicationConn();
                appConfig.SetCommunication(conn);
                conn.init(ip, port);
                //CommunicationConn.getInstance().init(AppData.userEntity.getUserName(),AppData.IP, AppData.port);
            }
        };
        AppStart.GetInstance().serverthread.start();
        Log.d(TAG, "onCreate() executed");




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "服务启动-------------------");
        AnterService.serviceInstans = this;
        AppStart.GetInstance().SetCommunication(conn);
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        AppStart.GetInstance().GetCommunicationConn().eDestory();
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }
}
