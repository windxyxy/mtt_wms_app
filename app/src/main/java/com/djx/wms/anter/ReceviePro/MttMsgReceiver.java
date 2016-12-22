package com.djx.wms.anter.ReceviePro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.djx.wms.anter.AppData;
import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.PickingAreaUp;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.main_login;
import com.djx.wms.anter.reple_task;
import com.djx.wms.anter.whselelct;

import java.net.URISyntaxException;

/**
 * Created by ${MonsetrQiao} on 2016/12/8.
 * Describe:
 */
public class MttMsgReceiver extends BroadcastReceiver {
    private Intent intent1;
    private Context mContext;
    private UserEntity mEntity;
    private int injType;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = AppStart.getmContext();
        mEntity = new UserEntity();
        injType = intent.getIntExtra("injType",0);

        Log.e("MttMsgReceiver", "userLoginState=" + mEntity.getLoginState());
        if (mEntity.getLoginState() == -11) {
            Toast.makeText(context, "-- 请先登录 --", Toast.LENGTH_LONG).show();
            intent1 = new Intent(context, main_login.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
            //注销广播
            mContext.unregisterReceiver(MttMsgReceiver.this);

        } else if (mEntity.getLoginState() == 1) {
            Toast.makeText(context, "-- 请选择仓库 --", Toast.LENGTH_LONG).show();
            intent1 = new Intent(context, whselelct.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
            mContext.unregisterReceiver(MttMsgReceiver.this);

        } else {
            Log.e("MttMsgReceiver","injType==="+injType);
            if (injType == 209){
                intent1 = new Intent(context, reple_task.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
                mContext.unregisterReceiver(this);
            }
            if (injType == 210){
                intent1 = new Intent(context, PickingAreaUp.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
                mContext.unregisterReceiver(this);
            }

        }
    }
}
