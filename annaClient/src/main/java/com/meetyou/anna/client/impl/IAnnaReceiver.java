package com.meetyou.anna.client.impl;

import com.meetyou.anna.plugin.AntiAnna;

/**
 * Created by Linhh on 17/6/13.
 */
@AntiAnna
public class IAnnaReceiver {

    public IAnnaReceiver(){
    }

    public void onMethodExit(String clazz, Object obj, String name, String rtype){
    }

    public Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype){
        return null;
    }

    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
        return false;
    }

    public int level(){
        return 0;
    }
}
