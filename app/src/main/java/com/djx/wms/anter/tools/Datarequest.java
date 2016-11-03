package com.djx.wms.anter.tools;

import android.util.Log;

import com.djx.wms.anter.AppStart;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import pers.lh.communication.TranCoreClass;

/**
 * Created by gfgh on 2016/3/16.
 */
public class Datarequest {


    public static  List<Hashtable> GetDataArrayList(String SQL) {

        List<Hashtable<String, String>> listhas = new ArrayList<Hashtable<String, String>>();
        List<Hashtable> olisthas = new ArrayList<Hashtable>();

        TranCoreClass tranCoreClass = null;
        tranCoreClass = (TranCoreClass) AppStart.GetInstance().GetCommunicationConn().SyncSend(203, SQL);
        if (tranCoreClass != null) {
            olisthas = ConnTranPares.GetDataList(tranCoreClass, Hashtable.class);
            return olisthas;
        } else{
            Log.d("loginActivity", "Get CGD Timeout.............");
            /*启动通讯服务*/
            return null;
        }

    }




    public static  List<Hashtable> GETstored(Hashtable data) {

        List<Hashtable<String, String>> listhas = new ArrayList<Hashtable<String, String>>();
        List<Hashtable> stored = new ArrayList<Hashtable>();


        TranCoreClass tranCoreClass = new TranCoreClass();
        tranCoreClass = (TranCoreClass) AppStart.GetInstance().GetCommunicationConn().SyncSend(207, data);
        if (tranCoreClass != null) {
            Log.d("Datarequest", "GETstored.............");
            stored = ConnTranPares.GetDataList(tranCoreClass, Hashtable.class);
           return stored;


        } else{
            Log.d("Datarequest", "GETstored Timeout.............");
            /*启动通讯服务*/
            return null;
        }

    }

  /*  public static  int GetDataint(String SQL) {

        int count=0;

        TranCoreClass tranCoreClass = null;
        tranCoreClass = (TranCoreClass) AppStart.GetInstance().GetCommunicationConn().SyncSend(203, SQL);
        if (tranCoreClass != null) {
            Log.d("loginActivity", "Get int Sucess.............");
            count = ConnTranPares.GetData(tranCoreClass,);

            return count;
        } else{
            Log.d("loginActivity", "Get CGD Timeout.............");
            *//*启动通讯服务*//*
            return null;
        }

    }*/






}
