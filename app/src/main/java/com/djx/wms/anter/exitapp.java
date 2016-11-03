package com.djx.wms.anter;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gfgh on 2016/4/14.
 */
public class exitapp extends Application {
    public List<Activity> activityList=new LinkedList<Activity>();
    private static exitapp instance;
    private exitapp()
    {
    }
    public static exitapp getInstance()
    {
        if(null == instance)
        {
            instance = new exitapp();
        }
        return instance;
    }
    public void addActivity(Activity activity)
    {
        activityList.add(activity);
    }

    public void exit()
    {

        for(Activity activity:activityList)
        {
            activity.finish();
        }

        System.exit(0);

    }
    public void clearActivity()
    {

        for(Activity activity:activityList)
        {
            activity.finish();
        }
    }
}