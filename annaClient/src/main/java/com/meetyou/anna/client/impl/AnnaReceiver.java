package com.meetyou.anna.client.impl;

import com.meetyou.anna.plugin.AntiAnna;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Linhh on 17/6/8.
 */
@AntiAnna
public class AnnaReceiver {
    private static Method mMethodGetMap;
    private static Object mMetasObject;
    private static ConcurrentHashMap<String, IAnnaReceiver> mObjectMap = new ConcurrentHashMap<>();

    public static void makeClazz() throws Exception {
        Class var0 = Class.forName("com.meetyou.anna.inject.support.AnnaInjectMetas");
        mMetasObject = var0.newInstance();
        mMethodGetMap = mMetasObject.getClass().getMethod("getMap");
    }

    public static IAnnaReceiver makeReceiver(String clazz) throws Exception{
        Class var0 = Class.forName(clazz);
        return (IAnnaReceiver)var0.newInstance();
    }

    public static void init(){

        try {

            if(mMethodGetMap == null) {
                makeClazz();
//                Log.w("AnnaReceiver","AnnaReceiver <init> is called");
//                HashMap<String, ArrayList<String>> map =  (HashMap<String, ArrayList<String>>)mMethodGetMap.invoke(mMetasObject);
//                for (Map.Entry<String,ArrayList<String>> entry : map.entrySet()) {
//                    Log.d("annamap-test","key:    " + entry.getKey());
//                    for(String configurationDO : entry.getValue()){
//                        Log.d("annamap-test",configurationDO.toString());
//                    }
//                }
            }


        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    private static  ArrayList<IAnnaReceiver> getReceiver(String clazz, String methodName) throws Exception{
        init();
        ArrayList<IAnnaReceiver> list = new ArrayList<>();
        HashMap<String, ArrayList<String>> map =  (HashMap<String, ArrayList<String>>)mMethodGetMap.invoke(mMetasObject);
        for (Map.Entry<String,ArrayList<String>> entry : map.entrySet()) {
            String[] s = entry.getKey().trim().split(" ");
            if(s[0].equals("**") || s[0].equals(clazz)){
                //所有接收
                if(Pattern.compile(s[1])
                        .matcher(methodName).matches()){
                    //匹配成功
                    if(entry.getValue() != null){
                        for(String v : entry.getValue()){
//                            Log.d("annamap-test",v);
                            IAnnaReceiver iAnnaReceiver = mObjectMap.get(v);
                            if(iAnnaReceiver == null){
                                iAnnaReceiver = makeReceiver(v);
                            }
                            mObjectMap.put(v, iAnnaReceiver);
                            list.add(iAnnaReceiver);
                        }
                    }
                }
            }

        }
        return list;
    }

    public static void onMethodExit(String clazz, Object obj, String name, Object[] objects, String rtype){
//        Log.w("AnnaReceiver","onMethodExit is called:" + clazz + ";" + name);
        try {
            ArrayList<IAnnaReceiver> iAnnaReceivers = getReceiver(clazz, name);
            if(iAnnaReceivers != null && iAnnaReceivers.size() > 0){
                iAnnaReceivers.get(0).onMethodExit(clazz, obj, name,rtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype){
//        Log.w("AnnaReceiver","onIntercept is called:" + clazz + ";" + name);
        try {
            ArrayList<IAnnaReceiver> iAnnaReceivers = getReceiver(clazz, name);
            if(iAnnaReceivers != null && iAnnaReceivers.size() > 0){
                return iAnnaReceivers.get(0).onIntercept(clazz, obj, name, objects,rtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
//        Log.d("AssassinReveiver","onMethodEnter is called:" + clazz + ";" + name);
        try {
            ArrayList<IAnnaReceiver> iAnnaReceivers = getReceiver(clazz, name);
            if(iAnnaReceivers != null && iAnnaReceivers.size() > 0){
                return iAnnaReceivers.get(0).onMethodEnter(clazz, obj, name, objects,rtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
