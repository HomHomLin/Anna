package com.meetyou.anna.client.impl;

import android.util.Log;

import com.meetyou.anna.plugin.AntiAssassin;

/**
 * Created by Linhh on 17/6/8.
 */
@AntiAssassin
public class AnnaReceiver {

    public void onMethodExit(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodExit is called:" + clazz + ";" + name);
    }

    public Object onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnter is called:" + clazz + ";" + name);
        return null;
    }

    public boolean onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnter is called:" + clazz + ";" + name);
        return false;
    }
}
