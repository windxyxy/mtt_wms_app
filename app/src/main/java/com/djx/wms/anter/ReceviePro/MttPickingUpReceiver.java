package com.djx.wms.anter.ReceviePro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.PickingAreaUp;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.main_login;
import com.djx.wms.anter.whselelct;

/**
 * Created by ${MonsetrQiao} on 2016/12/19.
 * Describe:拣货区上架消息弹窗通知
 */
public class MttPickingUpReceiver extends BroadcastReceiver {
    private Context mContext;
    private UserEntity mEntity;
    private Intent mIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = AppStart.getmContext();
        mEntity = new UserEntity();
        Log.e("MttPickingUpReceiver", "userLoginState=" + mEntity.getLoginState());
        if (mEntity.getLoginState() == -11) {
            Toast.makeText(context, "-- 请先登录 --", Toast.LENGTH_LONG).show();
            mIntent = new Intent(context, main_login.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
            //注销广播
            mContext.unregisterReceiver(MttPickingUpReceiver.this);

        } else if (mEntity.getLoginState() == 1) {
            Toast.makeText(context, "-- 请选择仓库 --", Toast.LENGTH_LONG).show();
            mIntent = new Intent(context, whselelct.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
            mContext.unregisterReceiver(MttPickingUpReceiver.this);

        } else {
            mIntent = new Intent(context, PickingAreaUp.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
            mContext.unregisterReceiver(this);
        }
    }
}
