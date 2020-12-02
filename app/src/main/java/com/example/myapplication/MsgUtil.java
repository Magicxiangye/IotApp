package com.example.myapplication;


import java.util.ArrayList;
import java.util.List;

public class MsgUtil {

    public static List<Msg> getMsgList(){

        List<Msg> msgList = new ArrayList<>();

        Msg msg = new Msg(1,R.drawable.user_background,

                "测试天气样式 *1",

                "多云");

        msgList.add(msg);

        msg = new Msg(2,R.drawable.user_background,

                "测试天气样式 *2",

                "这是天晴");

        msgList.add(msg);

        msg = new Msg(3,R.drawable.user_background,

                "测试天气样式 *3",

                "是大雨。");

        msgList.add(msg);

        msg = new Msg(4,R.drawable.user_background,

                "测试天气样式 *4",

                "雾霾");

        msgList.add(msg);

        msg = new Msg(5,R.drawable.user_background,

                "测试天气样式 *5",

                "多云阵雨");

        msgList.add(msg);

        return msgList;

    }

}
