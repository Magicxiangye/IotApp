package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class Fragment_one extends Fragment {

    //重写动态布局方法
   @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
       View view  = inflater.inflate(R.layout.fragment_one,container,false);
       return view;
   }

   //视频图片的点击事件
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        ImageButton mImage_video = (ImageButton)getActivity().findViewById(R.id.Image_Button_one);
        mImage_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_video_one = new Intent(getActivity(),Main2Activity.class);
                startActivity(intent_video_one);
            }
        });

        //测试点击事件（后面在这里修改）
        ImageButton mImage_test = getActivity().findViewById(R.id.Image_Button_two);
        mImage_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_test = new Intent(getActivity(),FlashActivity.class);
                startActivity(intent_test);
            }
        });


    }


}

