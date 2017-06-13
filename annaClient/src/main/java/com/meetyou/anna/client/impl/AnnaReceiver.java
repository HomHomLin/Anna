package com.meetyou.anna.client.impl;

import android.util.Log;

import com.meetyou.anna.plugin.AntiAssassin;

/**
 * Created by Linhh on 17/6/8.
 */
@AntiAssassin
public class AnnaReceiver {

    public AnnaReceiver(){
        Log.d("AssassinReveiver","AnnaReceiver <init> is called");
    }

    public void onMethodExit(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodExit is called:" + clazz + ";" + name);
    }

    public Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onIntercept is called:" + clazz + ";" + name);
        return null;
    }

    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnter is called:" + clazz + ";" + name);
        return false;
    }
}
