package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


/**
 * 点击返回键 将触发此类
 * 提供了布局文件，并为布局文件控件绑定监听器
 * @author Administrator
 *
 */

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
    }

    public void exitButtonYes(View v) {
        this.finish();
       Vedio_viewActivity.instance.finish();//结束VIDEOActivity
    }

    public void exitButtonNo(View v) {
        this.finish();// 结束本类
    }
}
