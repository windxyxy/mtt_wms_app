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
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.main_login;
import com.djx.wms.anter.reple_task;
import com.djx.wms.anter.whselelct;

/**
 * Created by ${MonsetrQiao} on 2016/12/8.
 * Describe:
 */
public class MttMsgReceiver extends BroadcastReceiver {
    private Intent intent1;
    private Context mContext;
    private UserEntity mEntity;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = AppStart.getmContext();
        mEntity = new UserEntity();
        Log.e("MttMsgReceiver", "userLoginState=" + mEntity.getLoginState());
        if (mEntity.getLoginState() == -11) {
            Toast.makeText(context, "-- 请先登录 --", Toast.LENGTH_LONG).show();

            intent1 = new Intent(context, main_login.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
            //注销广播
            mContext.unregisterReceiver(MttMsgReceiver.this);

//            new AlertDialog.Builder(context)
//                    .setTitle("提示")
//                    .setMessage("请先登录")
//                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            intent1 = new Intent(context, main_login.class);
//                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent1);
//                            //注销广播
//                            context.unregisterReceiver(MttMsgReceiver.this);
//                        }
//                    })
//                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            context.unregisterReceiver(MttMsgReceiver.this);
//                        }
//                    })
//                    .show();


        } else if (mEntity.getLoginState() == 1) {
            Toast.makeText(context, "-- 请选择仓库 --", Toast.LENGTH_LONG).show();
            intent1 = new Intent(context, whselelct.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
            mContext.unregisterReceiver(MttMsgReceiver.this);

//            new AlertDialog.Builder(context)
//                    .setTitle("提示")
//                    .setMessage("请选择仓库")
//                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            intent1 = new Intent(context, whselelct.class);
//                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent1);
//                            context.unregisterReceiver(MttMsgReceiver.this);
//                        }
//                    })
//                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            context.unregisterReceiver(MttMsgReceiver.this);
//                        }
//                    })
//                    .show();
        } else {
            intent1 = new Intent(context, reple_task.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
            mContext.unregisterReceiver(this);
        }
    }
}
