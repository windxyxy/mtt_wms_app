package com.djx.wms.anter.entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.qrcodelogin;

/**
 * Created by ${MonsetrQiao} on 2016/12/12.
 * Describe:开机启动服务
 */
public class OpenToService extends BroadcastReceiver {
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
//        mContext = AppStart.getmContext();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent1 = new Intent(context, qrcodelogin.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
