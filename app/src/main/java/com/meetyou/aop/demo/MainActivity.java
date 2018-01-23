package com.meetyou.aop.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.meetyou.anna.plugin.AnnaReceiver;
import com.meetyou.anna.plugin.AntiAnna;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportFragmentManager().getFragments().get(0).getFragmentManager().isVisible()
//        Log.d("test",testanna() + "");
        findViewById(R.id.btn).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @AntiAnna
            @Override
            public void onClick(View v) {

            }
        });
        boolean s = Pattern.compile("on[C]lick+")
                .matcher("onClick").matches();
        Log.d("test",s + "");
    }

    public void onClick(){
    }

    public static boolean testanna(){
        Log.v("test", "s");
        return true;
    }
}
