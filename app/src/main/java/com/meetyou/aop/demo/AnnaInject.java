package com.meetyou.aop.demo;

import java.lang.reflect.Method;

/**
 * Created by Linhh on 17/6/8.
 */
public class AnnaInject {
    private static AnnaInject mAnnaInject;
    public final static String ANNA_RECEIVER_CLASS = "com.meetyou.anna.client.impl.AnnaReceiver";
    private Object mAnnaReceiver;
    private Method mMethodEnter;
    private Method mMethodExit;
    private Method mOnIntercept;

    public static AnnaInject getInject() {
        if (mAnnaInject == null) {
            mAnnaInject = new AnnaInject();
        }
        return mAnnaInject;
    }

    public void makeClazz() throws Exception{
        Class clazz = Class.forName(ANNA_RECEIVER_CLASS);
        mAnnaReceiver = clazz.newInstance();
    }

    public boolean onIntercept() {
        try {
            if(mAnnaReceiver == null){
                makeClazz();
            }
            if(mOnIntercept == null) {
                mOnIntercept = mAnnaReceiver.getClass().getMethod("onIntercept");
            }
            return (boolean)mOnIntercept.invoke(mAnnaReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Object onMethodEnter(String clazzname, Object obj, String name, Object[] objects, String rtype) {
        try {
            if(mAnnaReceiver == null){
                makeClazz();
            }
            if(mMethodEnter == null) {
                mMethodEnter = mAnnaReceiver.getClass().getMethod("onMethodEnter", String.class, Object.class, String.class, Object[].class, String.class);
            }
            return mMethodEnter.invoke(mAnnaReceiver, clazzname, obj, name, objects, rtype);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onMethodExit(String clazzname, Object obj, String name, Object[] objects, String rtype) {
        try {
            if(mAnnaReceiver == null){
                makeClazz();
            }
            if(mMethodExit == null) {
                mMethodExit = mAnnaReceiver.getClass().getMethod("onMethodExit", String.class, Object.class, String.class, Object[].class, String.class);
            }
            mMethodExit.invoke(mAnnaReceiver, clazzname, obj, name, objects, rtype);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
