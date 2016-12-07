package com.djx.wms.anter.ReceviePro;

import java.util.Hashtable;

/**
 * Created by pc on 2016/3/10.
 */
public class OrderRegister {
    private static final OrderRegister instance = new OrderRegister();
    public static OrderRegister GetInstance(){
        return instance;
    }


    private Hashtable<String,IOrderTran> hash = new Hashtable<String,IOrderTran>();

    IOrderTran Get(String order){
        return hash.get(order);
    }

    public void Set(String order,IOrderTran method){
        hash.put(order, method);
    }

    public void Del(String order){
        hash.remove(order);
    }




}
