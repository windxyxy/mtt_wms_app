package com.djx.wms.anter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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
//    private DaoStatic mStatic;


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverconfig);

//        mStatic = new DaoStatic();

        et_config = (EditText) findViewById(R.id.config);
        et_password = (EditText) findViewById(R.id.configpass);

//        String thisIp = AppStart.GetInstance().getConfigIp();
//        String thisPort = AppStart.GetInstance().getConfigPort();

        String ip = AppStart.GetInstance().getIP();
        short port = AppStart.GetInstance().getPort();
        String po = "" + port + "";

        config = TransactSQL.instance.filterSQL(ip);
        passconfig = TransactSQL.instance.filterSQL(po);


        if (!config.equals("")) {
            et_config.setText(config);
            et_password.setText(passconfig);
        }

    }


    public void sava(View v) {


//        EditText config = (EditText) findViewById(R.id.config);
//        EditText password = (EditText) findViewById(R.id.configpass);
        String configs = "";
        String configpass = "";

        configs = TransactSQL.instance.filterSQL(et_config.getText().toString());
        configpass = TransactSQL.instance.filterSQL(et_password.getText().toString());

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

//        mStatic.setLog(false);

        if (AppStart.GetInstance().GetCommunicationConn() != null) {
            AppStart.GetInstance().GetCommunicationConn().Connector.SetEndPoint(configs, (short)(Integer.parseInt(configpass)));
            Boolean tranCoreClass;
            tranCoreClass = AppStart.GetInstance().GetCommunicationConn().AsyncSend(4, 1);
            Log.e("Boolean tranCoreClass","Boolean tranCoreClass=="+tranCoreClass);
            if (!tranCoreClass) {
                AlertDialog.Builder build = new AlertDialog.Builder(config.this);
                build.setMessage("保存失败").show();
                return;
            }
        }
        try {
            Thread.sleep(500);
            AlertDialog.Builder build = new AlertDialog.Builder(config.this);
            build.setMessage("保存成功").show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void back(View v) {
//        mStatic.setLog(true);
        Intent intent = new Intent();/*创建一个新的intent对象*/
      /*  intent.putExtra("name",name);
        intent.putExtra("pass",pass);*/
        intent.setClass(config.this, main_login.class);
       /* 设置Intent的源地址和目标地址*/
        startActivity(intent);    /*   调用startActivity方法发送意图给系统*/
        config.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            mStatic.setLog(true);
            Intent intent = new Intent();/*创建一个新的intent对象*/
      /*  intent.putExtra("name",name);
        intent.putExtra("pass",pass);*/
            intent.setClass(config.this, main_login.class);
       /* 设置Intent的源地址和目标地址*/
            startActivity(intent);    /*   调用startActivity方法发送意图给系统*/
            config.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}