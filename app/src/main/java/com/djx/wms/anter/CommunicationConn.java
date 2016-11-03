package com.djx.wms.anter;

import android.util.Log;

import com.djx.wms.anter.RecevieFun.ProSyncFun;
import com.djx.wms.anter.ReceviePro.OrderRegister;

import java.util.Objects;

import pers.lh.communication.CommunicationService;
import pers.lh.communication.ICommunicationConnector;
import pers.lh.communication.ICommunicationConnectorEvent;
import pers.lh.communication.TranCoreClass;


/**
 * Created by pc on 2016/3/9.
 */
public class CommunicationConn {
    public static final String TAG = "CommunicationConn";
    public ICommunicationConnector Connector ;

    public  boolean state=false;
//    private static CommunicationConn INSTANCE;

    public CommunicationConn (){
        Log.d(TAG, " ini CommunicationConn");
    }


    /*public static final CommunicationConn getInstance() {
        if(INSTANCE==null) {
            INSTANCE = new CommunicationConn();
        }
        return INSTANCE;
    }*/

    public void init(String ip,short port){
        ICommunicationConnectorEvent connectorEvent  = new CommunicationImpl();
        Connector = new CommunicationService(connectorEvent);
        Connector.BeginConnect(ip,port);
    }

    public ICommunicationConnector GetConnector(){
        return this.Connector;
    }

    /*
    * 销毁
    * */
    public void eDestory(){
        this.Connector.Close();
    }

    /*
    * 同步方法
    * 参数：1指令，2数据
    *
    */
    public Object SyncSend(int order, Object date) {
        return this.SyncSend(order,0,date);
    }

    /*
       * 异步方法
       * 参数：1指令，2数据
       *
       */
    public Boolean AsyncSend(int order, Object date) {
        return this.Connector.SendData(order, 0, date);

    }



    /*
    * 同步方法
    * 参数：1指令，2动作，3数据
    *
    */
    public Object SyncSend(int order,int active, Object date){
        this.Connector.SendData(order, active, date);
        OrderRegister.GetInstance().Set(String.valueOf(order), new ProSyncFun());
        ProSyncFun.resultT = null;
        TranCoreClass resultTs = null;
        int i = 0;
        while (true) {
            try
            {
                if (ProSyncFun.resultT != null)
                {
                    resultTs = ProSyncFun.resultT;
                    break;
                }
                if (i >= 300)
                    break;
                Thread.sleep(100);
                i++;
            }
            catch(Exception ex) {
                i = 200;
                break;
            }
        }
        ProSyncFun.resultT = null;
        OrderRegister.GetInstance().Del(String.valueOf(order));
        return resultTs;

    }

}
