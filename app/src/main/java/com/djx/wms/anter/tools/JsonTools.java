package com.djx.wms.anter.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Created by pc on 2016/3/16.
 */
public class JsonTools {
        public static String ToJson(Object o){
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            return gson.toJson(o);
        }

    public static <T> T ToObject(String json, Class<T> t){
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            return gson.fromJson(json, t);
        }
    }
