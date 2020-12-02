package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.InputStream;

import io.MjpegInputStream;
import tools.Generic;

public class FlashActivity extends AppCompatActivity {

    private Context mContext = this;
    private AutoCompleteTextView ipEdt = null;
    private EditText portEdt = null;
    private TextView hintTv = null;
    private DhcpInfo dpInfo = null;
    private WifiManager wifi = null;
    private InputStream is = null;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor = null;
    private String port = "8000";/*用来保存获得用户输入的端口*/


    private static final String[] VIDEO_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
    private static final int VIDEO_PERMISSIONS_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        requestPermission();
        init();


        //连接端口按钮的点击事件
        Button connet_Button = (Button)findViewById(R.id.server_logIn_Button);
        //监听
        connet_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEdt.getText().toString();/*获得输入的IP*/
                port = portEdt.getText().toString();/*获得输入的端口*/

                //port不能为空
                if (!port.equals("")&&checkAddr(ip, Integer.valueOf(port))) {
                    new ConnectTask().execute(ip);//建立连接
                } else {
                    Generic.showMsg(FlashActivity.this, "请检查ip和port", true);
                }
            }
        });

        //下拉菜单的点击事件
        ImageView history_users = (ImageView)findViewById(R.id.history_user);
        history_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipEdt.showDropDown();
            }
        });



    }


    //申请权限
    private void requestPermission() {
        // 当API大于 23 时，才动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(FlashActivity.this,VIDEO_PERMISSIONS,VIDEO_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VIDEO_PERMISSIONS_CODE:
                //权限请求失败
                if (grantResults.length == VIDEO_PERMISSIONS.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //弹出对话框引导用户去设置
                            showDialog();
                            Toast.makeText(FlashActivity.this, "请求权限被拒绝", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }else{
                    Toast.makeText(FlashActivity.this, "已授权", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //弹出提示框
    private void showDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("录像需要相机、录音和读写权限，是否去设置？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToAppSetting();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private  void init(){

        //获取在本Activity要使用的控件和WiFi
        hintTv = (TextView) findViewById(R.id.hintTv);
        ipEdt = (AutoCompleteTextView) findViewById(R.id.ip);
        portEdt = (EditText) findViewById(R.id.port);

        //wifi获取
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initSp();

    }

    //方便查找出以前登录过的IP
    private  void initSp(){
        sp = getSharedPreferences("config", MODE_PRIVATE);
        /*创建好配置文件后，以后就可以用它的edit来操作配置文件了*/
        editor = sp.edit();
        String names[] = sp.getString("ip", "").split(":");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, names);
        ipEdt.setAdapter(adapter);

        /**
         * 生成配置文件config，它在 /data/data/<package name>/shared_prefs/config.xml
         * 取出配置文件的ip用冒号隔开，并为自动完成列表设置适配器
         */

    }


    /**
     * 分割的ip是4段，ip端口范围在1000-65535
     * @param ip
     * @param port
     * @return
     */
    private boolean checkAddr(String ip, int port) {
        if (ip.split("\\.").length != 4)
            return false;
        if (port < 1000 || port > 65535)
            return false;

        return true;
    }

    //@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class ConnectTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < params.length; i++) {
                String ip = params[i];/* 取出每一个ip */

                if (ip.split("\\.").length == 4) {
                    /**
                     * 在浏览器观察画面时,也是输入下面的字符串网址
                     */
                    //String action = "http://" + ip + ":"+ port + "/?action=stream";
                    String action = "http://" + ip + ":"+ port + "/jpg_stream";
                    is = http(action);
                    if (is != null) { /*第一次必须输入IP，下次登录时才可找到之前登录成功后的IP*/
                        writeSp(ip);
                        MjpegInputStream.initInstance(is);
                        break;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (is != null) {
                /**
                 * Intent是Android特有的东西，可以在Intent指定程序要执行的动作(比如:view,edit,dial)
                 * 都准备好程序执行该工作所需要的材料后，只要调用startActivity，Android系统会自动寻找最符合你指定要求的应用程序
                 * 并执行该程序
                 */
                    startActivity(new Intent(FlashActivity.this, Vedio_viewActivity.class));
                finish();/*结束本Activity*/
            } else{
                hintTv.setText(getResources()
                        .getString(R.string.connect_failed));
                Generic.showMsg(mContext, "连接失败", true);
            }

            super.onPostExecute(result);
        }

        /**
         * 功能：http连接
         * Android提供两种http客户端， HttpURLConnection 和 Apache HTTP Client，它们都支持HTTPS，能上传和下载文件
         * 配置超时时间，用于IPV6和 connection pooling， Apache HTTP client在Android2.2或之前版本有较少BUG
         * HttpURLConnection是更好的选择，在这里我们用的是 Apache HTTP Client
         * 凡是对IO的操作都会涉及异常，所以要try和catch
         * @param url
         * @return InputStream
         */
        private InputStream http(String url) {
            HttpResponse res;
            DefaultHttpClient httpclient = new DefaultHttpClient();/*创建http客户端，才能调用它的各种方法*/
            httpclient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 500);/*设置超时时间*/

            try {
                HttpGet hg = new HttpGet(url);/*这是GET方法的http API， GET方法是默认的HTTP请求方法*/
                res = httpclient.execute(hg);
                return res.getEntity().getContent(); // 从响应中获取消息实体内容
            } catch (IOException e) {
            }

            return null;
        }

    }




    /**
     * 更新SharedPreferences
     * 1.先判断ip是否有"ip"值，没有就将传进来的data赋值给ip
     * 2.ip有值就取出，然后用冒号分隔开
     * 3.sp数组只能存放10组ip，如果超过了10组，先清零配置文件再更新
     * 4.遍历数组，如果已有当前登录成功的ip，则返回
     * 5.数组里不包含登录成功的ip，则将当前登录成功的ip添加至sp数组并提交
     * @paramip
     */

    private void writeSp(String data) {
        if(!sp.contains("ip")){
            editor.putString("ip", data);
            editor.commit();
            return;
        }

        /**
         * 配置文件里有ip，表示之前登录成功了
         */
        String ip = sp.getString("ip", "");
        String[] ips = ip.split(":");

        if(ips.length >= 10){
            editor.clear();
            editor.commit();
            editor.putString("ip", data);
            editor.commit();
            return;
        }

        for(int i=0; i<ips.length; i++){
            if(ips[i].equals(data))
                return;
        }
        editor.putString("ip", data+":"+ip);/*放在以前成功了的ip的前面*/
        editor.commit();
    }




}
