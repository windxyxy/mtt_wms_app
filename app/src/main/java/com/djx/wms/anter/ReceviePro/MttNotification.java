package com.djx.wms.anter.ReceviePro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.R;
import com.djx.wms.anter.TestActivity;
import com.djx.wms.anter.reple_task;

import pers.lh.communication.PostClass;
import pers.lh.communication.TranCoreClass;

/**
 * Created by ${MonsetrQiao} on 2016/12/6.
 * Describe:
 */
public class MttNotification implements IOrderTran {
    private int NOTIFY_ID_TEST = 0;
    private Context mContext;
    private int injType;
    private MttNotifi notifi;
    private OrderParse mParse;

    @Override
    public void Tran(TranCoreClass tcc) {
        notifi= new MttNotifi();
        mParse = new OrderParse();
        injType = mParse.inOrder;
        Log.e("mttInOrder","injType = "+injType);
        notifi.getNotification(injType);
//        mContext = AppStart.getmContext();
//        new Thread() {
//            @Override
//            public void run() {
//                if (AppStart.GetInstance().getUserID() == 0) {
//                } else if (AppStart.GetInstance().Warehouse == 0) {
//                } else {
//                    try {
//                        Thread.sleep(100);
//                        Log.e("MttNotifi", "MttNotification");
//                        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
//                        Intent intent = new Intent(mContext, reple_task.class);
//
//                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        Notification notification = builder
//                                .setContentTitle("任务通知")
//                                .setContentText("有新的补货任务，点击查看")
//                                .setWhen(System.currentTimeMillis())
//                                .setSmallIcon(R.mipmap.xinniu)
//                                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.xinniu))
//                                .setDefaults(Notification.DEFAULT_ALL)
//                                .setContentIntent(pendingIntent)
//                                .build();
//
//                        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后通知消失
//                        nm.notify(NOTIFY_ID_TEST, notification);
//                        NOTIFY_ID_TEST++;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();


    }
}
