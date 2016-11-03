package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.djx.wms.anter.tools.TransactSQL;

import java.io.File;

/**
 * Created by gfgh on 2016/3/9.
 */
public class config extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverconfig);

        String ip=AppStart.GetInstance().getIP();
        short  port=AppStart.GetInstance().getPort();

        String po=""+port+"";
        String name= TransactSQL.instance.filterSQL(ip);
        String  passconfig= TransactSQL.instance.filterSQL(po);

    /*    String name=AppStart.GetInstance().getIP();

        SharedPreferences sharedPreferencess = getSharedPreferences("serverconfig", 0);
        String ports =sharedPreferencess.getString("port", "");*/



        EditText config = (EditText) findViewById(R.id.config);
        EditText password = (EditText) findViewById(R.id.configpass);


        if (!name.equals("")) {
            config.setText(name);
            password.setText(passconfig);
        }


    }


    public void sava(View v) {


        EditText config = (EditText) findViewById(R.id.config);
        EditText password = (EditText) findViewById(R.id.configpass);
        String configs = config.getText().toString();
        String configpass = password.getText().toString();

        configs= TransactSQL.instance.filterSQL(configs);
        configpass= TransactSQL.instance.filterSQL(configpass);

        if(configs.equals("")||configpass.equals("")){
            AlertDialog.Builder build = new AlertDialog.Builder(config.this);
            build.setMessage("IP或端口不能为空！").show();
            return;
        }





        String path = Environment.getExternalStorageDirectory().getPath();
        File existence = new File(path+"/wmsconfig/config.txt");
        if (existence.isFile()) { // 判断是否是文件
            existence.delete();
        }


        AppStart.GetInstance().setIP(configs);
        AppStart.GetInstance().setPort(configpass);
        if(AppStart.GetInstance().GetCommunicationConn()!=null ){
            AppStart.GetInstance().GetCommunicationConn().Connector.SetEndPoint(configs, (short) (Integer.parseInt(configpass)));


            Boolean tranCoreClass;
            tranCoreClass = (Boolean) (AppStart.GetInstance().GetCommunicationConn().AsyncSend(4, 1));
            if (!tranCoreClass) {
                AlertDialog.Builder build = new AlertDialog.Builder(config.this);
                build.setMessage("退出失败").show();
            }
        }




        AlertDialog.Builder build = new AlertDialog.Builder(config.this);
        build.setMessage("保存成功").show();

    }


    public void back(View v) {
        Intent intent = new Intent();/*创建一个新的intent对象*/
      /*  intent.putExtra("name",name);
        intent.putExtra("pass",pass);*/
        intent.setClass(config.this, main_login.class);
       /* 设置Intent的源地址和目标地址*/
        startActivity(intent);    /*   调用startActivity方法发送意图给系统*/
        config.this.finish();
    }
}