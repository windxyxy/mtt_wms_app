package com.djx.wms.anter.ReceviePro;

import android.app.Activity;
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
import com.djx.wms.anter.buttom_state;
import com.djx.wms.anter.reple_task;

import java.util.Hashtable;
import java.util.List;

import pers.lh.communication.PostClass;

/**
 * Created by ${MonsetrQiao} on 2016/12/6.
 * Describe:
 */
public class MttNotifi {
    private static int NOTIFY_ID_TEST = 0;
    private Context mContext;
    private String title = "";
    private String text = "";

    public void getNotification(int injType){
        mContext = AppStart.getmContext();
        if (injType == 209){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    if (AppStart.GetInstance().getUserID() == 0) {
                    } else if (AppStart.GetInstance().Warehouse == 0) {
                    } else {
                        try {
                            Thread.sleep(100);
                            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                            Intent intent = new Intent(mContext, reple_task.class);

                            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification notification = builder
                                    .setContentTitle("任务通知")
                                    .setContentText("有新的补货任务，点击查看")
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.mipmap.xinniu)
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.xinniu))
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(pendingIntent)
                                    .build();

                            notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后通知消失
                            nm.notify(NOTIFY_ID_TEST, notification);
                            NOTIFY_ID_TEST++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }


    }
}
