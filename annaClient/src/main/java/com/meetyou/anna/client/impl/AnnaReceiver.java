package com.meetyou.anna.client.impl;

import android.util.Log;

import com.meetyou.anna.plugin.AntiAssassin;

/**
 * Created by Linhh on 17/6/8.
 */
@AntiAssassin
public class AnnaReceiver {

    public void onMethodEnd(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnd is called");
    }

    public void onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnter is called");
    }
}
