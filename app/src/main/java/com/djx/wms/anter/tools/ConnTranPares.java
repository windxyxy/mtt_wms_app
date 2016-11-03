package com.djx.wms.anter.tools;

import android.util.Log;

import com.djx.wms.anter.AppStart;
import com.djx.wms.anter.tools.JsonTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pers.lh.communication.TranCoreClass;

/**
 * Created by lh on 2016/3/16.
 */
public class ConnTranPares {
    ///获取通讯数据
    public static <T> T GetData(TranCoreClass tcc, Class<T> t) {

        return JsonTools.ToObject(tcc.PostClassT.get(0).CoreObject, t);
    }


    public static <T> List<T> GetDataListeee(TranCoreClass tcc, Class<T[]> type) {
        String json = tcc.PostClassT.get(0).CoreObject;
        T[] list = new Gson().fromJson(json, type);
        return Arrays.asList(list);
    }

    public static <T> ArrayList<T> GetDataList(TranCoreClass tcc, Class<T> cls) {
        String json = tcc.PostClassT.get(0).CoreObject;
        ArrayList<T> mList = new ArrayList<T>();

        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            mList.add(new GsonBuilder()
                    .serializeNulls()//处理Null
                    .create()
                    .fromJson(elem, cls));
        }
        return mList;
    }


}
