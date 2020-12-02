package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private ListView listView;
    private List<Msg> msgList;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //卡片式布局的适配器实例化
        listView = findViewById(R.id.listview);
        msgList = MsgUtil.getMsgList();
        adapter = new MsgAdapter(msgList,this);
        listView.setAdapter(adapter);

         // 悬浮窗口的点击事件（后期用于刷新视频作用）
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"是否需要刷新天气",Snackbar.LENGTH_LONG)
                          .setAction("YES", new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  //点击完之后的反馈
                                  Toast.makeText(Main2Activity.this,"成功",Toast.LENGTH_LONG).show();
                              }
                          }).show();
            }
        });




    }



}
