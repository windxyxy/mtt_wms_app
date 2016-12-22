package com.djx.wms.anter.ReceviePro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
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
    private int injType;
    private MttNotifi notifi;
    private OrderParse mParse;
    private Context mContext;

    private PowerManager pm;//电源管理器
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void Tran(TranCoreClass tcc) {
        mContext = AppStart.getmContext();
        notifi= new MttNotifi();
        mParse = new OrderParse();
        injType = mParse.inOrder;
        Log.e("MttNotification","injType = "+injType);

        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //点亮屏幕
        //亮屏状态不执行此操作，否则点亮屏幕
        if (pm.isScreenOn()){
            //无操作
        }else {
            mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"wakeup");
            //超时锁，10秒后自动释放
            mWakeLock.acquire(10000);
        }
        notifi.getNotification(injType);

    }
}
