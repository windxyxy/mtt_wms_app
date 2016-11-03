package com.djx.wms.anter.RecevieFun;

import com.djx.wms.anter.ReceviePro.IOrderTran;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pers.lh.communication.TranCoreClass;


/**
 * Created by pc on 2016/3/10.
 */
public class ProSyncFun implements IOrderTran {
    public static ReadWriteLock lock = new ReentrantReadWriteLock(false);
    public static TranCoreClass resultT = null;
    @Override
    public void Tran(TranCoreClass tcc) {

        try
        {
            lock.writeLock().lock();
            resultT =tcc;
        }
        finally {
            lock.writeLock().unlock();
        }

        // TODO Auto-generated method stub
        //  FinalManager.GetGPSFlag =  tcc.PostClassT.get(0).CoreObject;

        //  AutoRegResult autoRegResult = JsonTools.ToObject(tcc.PostClassT.get(0).CoreObject, AutoRegResult.class);

        //  LocationServer.GetInstance().LocationRequest();
    }

}
