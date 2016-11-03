package com.djx.wms.anter.ReceviePro;

/**
 * Created by pc on 2016/3/10.
 */
public class OrderThreadWaitNotify {
    private static final OrderThreadWaitNotify intance = new OrderThreadWaitNotify();

    private MonitorObject mo = new MonitorObject();

    public static OrderThreadWaitNotify getIntance(){
        return intance;
    }

    ///挂起
    void DoWait(){
        synchronized(mo){
            try{
                mo.wait();
            }catch(Exception ex){}
        }
    }

    ///通知挂起的
    public void DoNotify(){
        synchronized(mo){
            mo.notify();
        }
    }

    class MonitorObject{}
}
