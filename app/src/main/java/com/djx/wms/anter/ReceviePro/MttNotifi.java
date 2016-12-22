package com.djx.wms.anter.ReceviePro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.R;

/**
 * Created by ${MonsetrQiao} on 2016/12/6.
 * Describe:
 */
public class MttNotifi {
    private static int NOTIFY_ID_TEST = 0;
    private Context mContext;
    private String ACTION = "MTT_MSG_ACTION";

    public void getNotification(final int injType) {
        mContext = AppStart.getmContext();
        if (injType == 209) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (AppStart.GetInstance().getUserID() == 0) {
                    } else if (AppStart.GetInstance().Warehouse == 0) {
                    } else {
                        try {
                            Thread.sleep(200);
                            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

                            /*
                            * 注册广播，检测用户是否登录或者选择仓库
                            * */
                            MttMsgReceiver receiver = new MttMsgReceiver();
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(ACTION);
                            mContext.registerReceiver(receiver, filter);

                            Intent intent = new Intent();
                            intent.setAction(ACTION);
                            intent.putExtra("injType",injType);
//                            mContext.sendBroadcast(intent);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
//                            NOTIFY_ID_TEST++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        if (injType == 210){
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (AppStart.GetInstance().getUserID() == 0) {
                    } else if (AppStart.GetInstance().Warehouse == 0) {
                    } else {
                        try {
                            Thread.sleep(200);
                            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

                            /*
                            * 注册广播，检测用户是否登录或者选择仓库
                            * */
                            MttMsgReceiver receiver = new MttMsgReceiver();
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(ACTION);
                            mContext.registerReceiver(receiver, filter);

                            Intent intent = new Intent();
                            intent.setAction(ACTION);
                            intent.putExtra("injType",injType);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification notification = builder
                                    .setContentTitle("任务通知")
                                    .setContentText("有新的拣货区上架任务，点击查看")
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.mipmap.xinniu)
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.xinniu))
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentIntent(pendingIntent)
                                    .build();

                            notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后通知消失
                            nm.notify(1, notification);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
}
