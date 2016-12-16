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
    private EditText et_config, et_password;
    private String config, passconfig;
    //-----


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverconfig);

        et_config = (EditText) findViewById(R.id.config);
        et_password = (EditText) findViewById(R.id.configpass);

//        String thisIp = AppStart.GetInstance().getConfigIp();
//        String thisPort = AppStart.GetInstance().getConfigPort();

        String ip = AppStart.GetInstance().getIP();
        short port = AppStart.GetInstance().getPort();
        String po = "" + port + "";

        config = TransactSQL.instance.filterSQL(ip);
        passconfig = TransactSQL.instance.filterSQL(po);


        if (!config.equals("") && !passconfig.equals("")) {
            et_config.setText(config);
            et_password.setText(passconfig);
        }

    }


    public void sava(View v) {


        EditText config = (EditText) findViewById(R.id.config);
        EditText password = (EditText) findViewById(R.id.configpass);
        String configs = "";
        String configpass = "";

        configs = TransactSQL.instance.filterSQL(config.getText().toString());
        configpass = TransactSQL.instance.filterSQL(password.getText().toString());

        if (configs.equals("") || configpass.equals("")) {
            AlertDialog.Builder build = new AlertDialog.Builder(config.this);
            build.setMessage("IP或端口不能为空！").show();
            return;
        }
        String path = Environment.getExternalStorageDirectory().getPath();
        File existence = new File(path + "/wmsconfig/config.txt");
        if (existence.isFile()) { // 判断是否是文件
            existence.delete();
        }

        AppStart.GetInstance().setIP(configs);
        AppStart.GetInstance().setPort(configpass);
        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            AppStart.GetInstance().GetCommunicationConn().Connector.SetEndPoint(configs, Integer.parseInt(configpass));
            Boolean tranCoreClass;
            tranCoreClass = AppStart.GetInstance().GetCommunicationConn().AsyncSend(4, 1);
            if (!tranCoreClass) {
                AlertDialog.Builder build = new AlertDialog.Builder(config.this);
                build.setMessage("退出失败").show();
                return;
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