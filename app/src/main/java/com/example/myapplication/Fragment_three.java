package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class Fragment_three extends Fragment {


    //重写动态布局方法
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        return view;
    }

     //Button 选项的点击事件
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getActivity方法
        Button button_one = (Button) getActivity().findViewById(R.id.Button_menu_option);
        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //活动转换
                Intent intent_two = new Intent(getActivity(),Main2Activity.class);
                startActivity(intent_two);
            }
        });
    }



}
