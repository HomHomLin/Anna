package com.meetyou.aop.demo;

import android.util.Log;

import com.meetyou.anna.client.impl.IAnnaReceiver;
import com.meetyou.anna.plugin.AnnaReceiver;

/**
 * Created by Linhh on 17/6/14.
 */
@AnnaReceiver("** on[C]reate+")
public class TestReceiver extends IAnnaReceiver{
    @Override
    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype) {
        Log.d("TestReceiver", "onMethodEnter:" + clazz + ";" + name);
        return super.onMethodEnter(clazz, obj, name, objects, rtype);
    }

    @Override
    public void onMethodExit(String clazz, Object obj, String name, String rtype) {
        super.onMethodExit(clazz, obj, name, rtype);
        Log.d("TestReceiver", "onMethodExit:" + clazz + ";" + name);
    }

    @Override
    public Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype) {
        Log.d("TestReceiver", "onIntercept:" + clazz + ";" + name);
        return super.onIntercept(clazz, obj, name, objects, rtype);
    }
}
