package com.djx.wms.anter.ReceviePro;

import android.util.Log;

import pers.lh.communication.PostClass;
import pers.lh.communication.TranCoreClass;


/**
 * Created by pc on 2016/3/10.
 */
public class OrderParse {
    public static int inOrder;
    private static final OrderParse intance = new OrderParse();
    private boolean WHILEFLAG = true;
    public static volatile boolean isWait = false;

    public static OrderParse GetInstance() {
        return intance;
    }

    public void Run() {
        ProData();
    }

    TranCoreClass tcc = null;

    void ProData() {
        while (WHILEFLAG) {
            tcc = RecevieQueue.getInstance().Get();
            if (tcc == null) {
                isWait = true;
                Log.d(OrderParse.class.getName(), "消息循环处理线程 挂起");
                OrderThreadWaitNotify.getIntance().DoWait();
            } else if (isWait)
                isWait = false;
            Log.d(OrderParse.class.getName(), "消息循环处理线程 执行");
            ///开始处理
            if (tcc != null)
                InvokeMethod(tcc);
        }

        Log.e(OrderParse.class.getName(), "退出消息循环处理线程");
    }

    void ExitWhile() {
        this.WHILEFLAG = false;
    }

    void InvokeMethod(TranCoreClass tcc) {
        for (PostClass pc : tcc.PostClassT) {
            Log.e(OrderParse.class.getName(), "处理指令:" + String.valueOf(pc.getInjType()) + ";指令体:" + pc.getCoreObject());
            IOrderTran ot = OrderRegister.GetInstance().Get(String.valueOf(pc.getInjType()));
            inOrder = pc.getInjType();
            Log.e("inOder", "inOrder = " + inOrder);
            if (ot != null) {
                ot.Tran(tcc);
            } else {
                Log.e(OrderParse.class.getName(), "指令" + String.valueOf(pc.getInjType()) + " 没有找到对应的方法:");

            }
        }
    }
}
