package com.djx.wms.anter;

import com.djx.wms.anter.ReceviePro.OrderRegister;

/**
 * Created by lh on 2016/3/10.
 * 注册服务发送的指令
 * 指令参考后台，
 * 注册的方法必须实现IOrderTran
 * 所有的处理方法放在RecevieFun包里面
 */
public class OrderManager {
    static final int ORDER_DEL_ACK = 21;

    OrderManager(){
/*
        指令参考后台  ,  方法必须实现IOrderTran
        参考com.djx.wms.anter.RecevieFun.ProLocation
        OrderRegister.GetInstance().Set(AppData.Order_AutoReg, new ProRegisterComplete());
        OrderRegister.GetInstance().Set(AppData.Order_Lock_Screen, new ProLockScreen());
        OrderRegister.GetInstance().Set(AppData.Order_unlock_Screen, new ProUnLockScreen());
        OrderRegister.GetInstance().Set(AppData.Order_GPS, new ProLocation());*/
    }
}
