package com.djx.wms.anter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.djx.wms.anter.ReceviePro.OrderParse;
import com.djx.wms.anter.ReceviePro.OrderThreadWaitNotify;
import com.djx.wms.anter.ReceviePro.RecevieQueue;
import com.djx.wms.anter.entity.FinalManager;
import com.djx.wms.anter.entity.UserEntity;
import com.djx.wms.anter.tools.ConnTranPares;

import pers.lh.communication.ICommunicationConnector;
import pers.lh.communication.ICommunicationConnectorEvent;
import pers.lh.communication.TranCoreClass;


/**
 * Created by pc on 2016/3/9.
 */
public class CommunicationImpl implements ICommunicationConnectorEvent {

    public static final String TAG = "CommunicationImpl";
    @Override
    public void ConnectClose(ICommunicationConnector arg0, String arg1) {

        /*if(AppStart.GetInstance().GetCommunicationConn().Connector.GetTid().equals("")){
        }*/
        Intent intent = new Intent("commnication.status");
        intent.putExtra("status", 629);
        AnterService.GetServiceInstans().sendBroadcast(intent);
        Log.d(TAG, "ConnectClose");

    }

    @Override
    public void ReceiveData(TranCoreClass tranCoreClass) {
        Intent intent = new Intent("commnication.status");
        intent.putExtra("status", 100);
        AnterService.GetServiceInstans().sendBroadcast(intent);
        Log.d(TAG, "ConnectClose");

        if(tranCoreClass!=null){
            RecevieQueue.getInstance().Add(tranCoreClass);
            if(OrderParse.isWait)
            {
                Log.d(TAG, "通知处理线程");
                OrderThreadWaitNotify.getIntance().DoNotify();
            }
        }
    }

    @Override
    public void ConnectFail(ICommunicationConnector arg0, String arg1) {
        Intent intent = new Intent("commnication.status");
        intent.putExtra("status",102);
        AnterService.GetServiceInstans().sendBroadcast(intent);
        Log.d(TAG, "ConnectFail");
    }

    @Override
    public void ConnectSucess(ICommunicationConnector arg0) {

        AppStart.GetInstance().GetCommunicationConn().state=true;

        // Intent intent = new Intent("commnication.status");
        // intent.putExtra("status", 8);
        // AnterService.GetServiceInstans().sendBroadcast(intent);



         ///发送数据
        ///AppStart.GetInstance().GetCommunicationConn();
        Intent intent = new Intent("commnication.status");
        intent.putExtra("status", 200);
        AnterService.GetServiceInstans().sendBroadcast(intent);
        Log.d(TAG, "ConnectClose");


        if(!AppStart.GetInstance().GetCommunicationConn().Connector.GetTid().equals("")){
            TranCoreClass tranCoreClass = null;
            tranCoreClass =   (TranCoreClass)(AppStart.GetInstance().GetCommunicationConn().SyncSend(0,1));
            return;
        }


        TranCoreClass tranCoreClass = null;
        AppStart.GetInstance().GetCommunicationConn().Connector.SetVersion(AppStart.GetInstance().version);
        tranCoreClass =   (TranCoreClass)(AppStart.GetInstance().GetCommunicationConn().SyncSend(3, AppStart.GetInstance().GetUserEntity()));


        if(tranCoreClass!=null) {
            if(tranCoreClass.getResult()==0)
            {
                UserEntity usere = ConnTranPares.GetData(tranCoreClass, UserEntity.class);
                if(usere!=null) {
                    usere.setUserID(usere.UserID);
                    usere.setUserPass(AppStart.GetInstance().GetUserEntity().getUserPass());
                    AppStart.GetInstance().SetUserEntity(usere);
                }

                if(AppStart.GetInstance().GetCommunicationConn().Connector.GetTid().length()==0) {
                   /* AppStart.GetInstance().handlers.removeCallbacks(AppStart.GetInstance().runnable);*/
                    AppStart.GetInstance().GetCommunicationConn().Connector.SetTid(tranCoreClass.getToken());
                    AppStart.GetInstance().GetCommunicationConn().Connector.BeginSendHeart();
                }
            }

            Message message=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("status", String.valueOf(tranCoreClass.getResult()));
            bundle.putString("message", String.valueOf(tranCoreClass.getMessage()));
            message.setData(bundle);
            message.what= FinalManager.LoginResultTag;

            if(AppStart.GetInstance().loginpage.equals("main_login")){
                AppStart.GetInstance().HandlerSendMessage(FinalManager.main_login,message);
            }else{
                AppStart.GetInstance().HandlerSendMessage(FinalManager.qrcdelogin,message);
            }


            Log.d(TAG, "LoginComplete");
            Log.d(TAG, tranCoreClass.getToken());
            Log.d(TAG, String.valueOf(tranCoreClass.getResult()));
        }
        else
        {
            Intent intents = new Intent("commnication.status");
            intents.putExtra("status", 408);
            AnterService.GetServiceInstans().sendBroadcast(intents);
            Log.d(TAG, "login timeout");
        }
    }

}
