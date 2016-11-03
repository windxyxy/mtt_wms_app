package com.djx.wms.anter.ReceviePro;

/**
 * Created by pc on 2016/3/10.
 */
public class OrderProThread {
    private static final OrderProThread instance = new OrderProThread();
    public static OrderProThread GetInstance(){
        return instance;
    }
    Thread orderParseThread = null;
    public OrderProThread(){
        orderParseThread = new Thread(){
            @Override
            public void run(){
                OrderParse.GetInstance().Run();
            }
        };
    }

    public void BeginThread(){
        orderParseThread.start();
    }

    public void CloseThread(){
        OrderParse.GetInstance().ExitWhile();
        OrderThreadWaitNotify.getIntance().DoNotify();
    }



}
