package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar mBottomNavigationBar;
    private TextBadgeItem textBadgeItem;
    private ShapeBadgeItem shapeBadgeItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setView();

    }



    /*初始化底部菜单栏*/

    private void initView(){
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        textBadgeItem = new TextBadgeItem();

        //显示信息提示数字
        textBadgeItem.setBorderWidth(4)
                .setBackgroundColor(R.color.colorAccent)
                .setAnimationDuration(200)
                .setText("99")
                .setHideOnSelect(false);
        //显示信息提示图形
        shapeBadgeItem = new ShapeBadgeItem();
        shapeBadgeItem.setShapeColorResource(R.color.colorPrimary)
                      .setGravity(Gravity.TOP|Gravity.END)
                      .setHideOnSelect(false);
    }

    //配置控件
    @SuppressLint("ResourceType")
    private void setView(){
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //mBottomNavigationBar.setInActiveColor("#436EEE");

        //底部控件的设置
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_bottom_download_press_down,"Download")
                            .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_bottom_download_press))
                            .setBadgeItem(textBadgeItem))
                            .addItem(new BottomNavigationItem(R.drawable.ic_bottom_av_press_down,"Video")
                             .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_bottom_av_press))
                             .setBadgeItem(textBadgeItem))
                            .addItem(new BottomNavigationItem(R.drawable.ic_bottom_menu_press_down,"Menu")
                             .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_bottom_menu_press))
                             .setBadgeItem(textBadgeItem))
                             .setFirstSelectedPosition(0)//设置第一个为选中状态
                             .initialise();//必须调用这个方法才能生效

        //设置第一个要显示的Fragment
         replace(new Fragment_one());
        //BottomNavigationBar的选择卡，选择事件，用监听的方法来进行
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                //未选中-->选中的switch函数
                switch (position){
                    case 0:
                        replace(new Fragment_one());
                        break;
                    case 1:
                        replace(new Fragment_two());
                        break;
                    case 2:
                        replace(new Fragment_three());
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onTabUnselected(int position) {
              //选中到未选中
            }

            @Override
            public void onTabReselected(int position) {
             //选中到选中
            }
        });

    }


    //切换frament函数
    private void replace(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction =  fm.beginTransaction();
        transaction.replace(R.id.frameContent,fragment);
        transaction.commit();
    }

    //启动activity
    public static void startActivity(Context context){
        Intent intent = new Intent();
        intent.setClass(context,MainActivity.class);
        context.startActivity(intent);

    }


}
