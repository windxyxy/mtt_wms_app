package com.djx.wms.anter.ReceviePro;

import android.util.Log;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import pers.lh.communication.TranCoreClass;

/**
 * Created by pc on 2016/3/10.
 */
public class RecevieQueue{
    private static final RecevieQueue instance = new RecevieQueue();
    static final String LOG_TAG = "ini";

    private Queue<TranCoreClass> queue = new LinkedBlockingQueue<TranCoreClass>();


    public static RecevieQueue getInstance(){
        return instance;
    }



    static {
        Log.d(LOG_TAG, "2");

    }

    {
        Log.d(LOG_TAG, "3");
    }

    public RecevieQueue(){
        Log.d(LOG_TAG, "4");

    }

    public boolean Add(TranCoreClass tranCoreClass){
        return queue.offer(tranCoreClass);
    }

    public TranCoreClass Get(){
        return queue.poll();
    }

    public int Count(){
        return queue.size();
    }

    public void Clear(){
        queue.clear();
    }
}