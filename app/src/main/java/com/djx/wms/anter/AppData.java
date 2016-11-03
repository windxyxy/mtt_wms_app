package com.djx.wms.anter;

import com.djx.wms.anter.entity.UserEntity;

/**
 * Created by pc on 2016/3/10.
 */
public class AppData {


    /*用户的实体*/
    public static UserEntity userEntity;
    /*IP*/
    public static  String IP;
    /*port*/
    public static short port;

    public static UserEntity getUserEntity() {
        return AppData.userEntity;
    }

    public static void setUserEntity(UserEntity userEntity) {
        AppData.userEntity = userEntity;
    }

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        AppData.IP = IP;
    }

    public static short getPort() {
        return port;
    }





    public static void setPort(short port) {
        AppData.port = port;
    }

    public static final String Configkey_TID = "Configkey_TID";
    public static final String Configkey_SN = "Configkey_SN";

    public static final String Configkey_ServerIP = "Configkey_ServerIP";
    public static final String Configkey_ServerPort = "Configkey_ServerP ort";
    public static final String Configkey_Username = "Configkey_Username";


    public static final String Order_AutoReg = "51";
    public static final String Order_RegConnect = "53";

    public static final String Order_Lock_Screen = "200";
    public static final String Order_unlock_Screen = "201";
    public static final String Order_GPS = "204";
    public static final String Order_GPS_Return = "208";

    /*上传的gps的flag*/
    public static String GetGPSFlag = "";


    /*主线程Handler通信常量*/
    public static final int Handler_RegComplete = 1;

}

