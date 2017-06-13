package com.meetyou.aop.demo;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Linhh on 17/6/8.
 */
public class AnnaInject2 {
    public static final String ANNA_RECEIVER_CLASS = "com.meetyou.anna.client.impl.AnnaReceiver";
    private static Object mAnnaReceiver;
    private static Method mMethodEnter;
    private static Method mMethodExit;
    private static Method mOnIntercept;

    public AnnaInject2() {
    }

    public static void makeClazz() throws Exception {
        Class var0 = Class.forName("com.meetyou.anna.client.impl.AnnaReceiver");
        mAnnaReceiver = var0.newInstance();
    }

    public static boolean onMethodEnter(String var0, Object var1, String var2, Object[] var3, String var4) {
        synchronized (AnnaInject2.class) {
            try {
                if (mAnnaReceiver == null) {
                    makeClazz();
                }

                if (mMethodEnter == null) {
                    mMethodEnter = mAnnaReceiver.getClass().getMethod("onMethodEnter", new Class[]{String.class, Object.class, String.class, Object[].class, String.class});
                }

                return ((Boolean) mMethodEnter.invoke(mAnnaReceiver, new Object[]{var0, var1, var2, var3, var4})).booleanValue();
            } catch (Exception var6) {
                Log.e("AnnaInject", "Anna is error");
                var6.printStackTrace();
                return false;
            }
        }
    }

    public static Object onIntercept(String var0, Object var1, String var2, Object[] var3, String var4) {
        synchronized (AnnaInject2.class) {
            try {
                if (mAnnaReceiver == null) {
                    makeClazz();
                }

                if (mOnIntercept == null) {
                    mOnIntercept = mAnnaReceiver.getClass().getMethod("onIntercept", new Class[]{String.class, Object.class, String.class, Object[].class, String.class});
                }

                return mOnIntercept.invoke(mAnnaReceiver, new Object[]{var0, var1, var2, var3, var4});
            } catch (Exception var6) {
                Log.e("AnnaInject", "Anna is error");
                var6.printStackTrace();
                return null;
            }
        }
    }

    public static void onMethodExit(String var0, Object var1, String var2, Object[] var3, String var4) {
        synchronized (AnnaInject2.class) {
            try {
                if (mAnnaReceiver == null) {
                    makeClazz();
                }

                if (mMethodExit == null) {
                    mMethodExit = mAnnaReceiver.getClass().getMethod("onMethodExit", new Class[]{String.class, Object.class, String.class, Object[].class, String.class});
                }

                mMethodExit.invoke(mAnnaReceiver, new Object[]{var0, var1, var2, var3, var4});
            } catch (Exception var6) {
                Log.e("AnnaInject", "Anna is error");
                var6.printStackTrace();
            }
        }

    }
}
