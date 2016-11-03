package com.djx.wms.anter.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;

import com.djx.wms.anter.R;
import com.djx.wms.anter.buttom_state;
import com.djx.wms.anter.many_goods;
import com.djx.wms.anter.query_library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gfgh on 2016/3/24.
 */
public class TransactSQL extends buttom_state{
    public static  TransactSQL instance = new TransactSQL();

    public static String filterSQL(String sInput) {

        String nextsInput=sInput;
        sInput = sInput.replaceAll("(\\^|\\*|and|exec|insert|select|delete|update|count|master|truncate|declare|char\\(|mid\\(|chr\\(|\\'|or)","");
        if(!sInput.equals(nextsInput)){
            sInput="";
        }

        String NoNullStr=sInput.trim();
        return NoNullStr;
    }
}
