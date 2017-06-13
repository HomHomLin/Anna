package com.meetyou.aop.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d("test",testanna() + "");
        String s = "ty[/space*]pe2";
        s = s.replace("[/space*]", " ");
        Log.d("test",s);
    }

    public static boolean testanna(){
        Log.v("test", "s");
        return true;
    }
}
