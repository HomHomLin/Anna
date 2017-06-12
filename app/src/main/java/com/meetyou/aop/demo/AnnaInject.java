package com.meetyou.aop.demo;

import java.lang.reflect.Method;

/**
 * Created by Linhh on 17/6/8.
 */
public class AnnaInject {
    public final static String ANNA_RECEIVER_CLASS = "com.meetyou.anna.client.impl.AnnaReceiver";
    private static Object mAnnaReceiver;
    private static Method mMethodEnter;
    private static Method mMethodExit;
    private static Method mOnIntercept;

    private static void makeClazz() throws Exception{
        Class clazz = Class.forName(ANNA_RECEIVER_CLASS);
        mAnnaReceiver = clazz.newInstance();
    }

    public static boolean onIntercept(String clazzname, Object obj, String name, Object[] objects, String rtype) {
        try {
            if(mAnnaReceiver == null){
                makeClazz();
            }
            if(mOnIntercept == null) {
                mOnIntercept = mAnnaReceiver.getClass().getMethod("onIntercept", String.class, Object.class, String.class, Object[].class, String.class);
            }
            return (Boolean)mOnIntercept.invoke(mAnnaReceiver, clazzname, obj, name, objects, rtype);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object onMethodEnter(String clazzname, Object obj, String name, Object[] objects, String rtype) {
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

    public static void onMethodExit(String clazzname, Object obj, String name, Object[] objects, String rtype) {
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
