package com.meetyou.anna.client.impl;

import android.util.Log;

import com.meetyou.anna.plugin.AntiAssassin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Linhh on 17/6/8.
 */
@AntiAssassin
public class AnnaReceiver {
    private Object mAnnaMetas;
    private Method mMethodGetMap;
    private HashMap<String, ArrayList<String>> mMap = null;
    private HashMap<String, IAnnaReceiver> mObjectMap = new HashMap<>();

    public void makeClazz() throws Exception {
        Class var0 = Class.forName("com.meetyou.anna.inject.support.AnnaInjectMetas");
        mAnnaMetas = var0.newInstance();
    }

    public IAnnaReceiver makeReceiver(String clazz) throws Exception{
        Class var0 = Class.forName(clazz);
        return (IAnnaReceiver)var0.newInstance();
    }

    public AnnaReceiver(){
        Log.w("AnnaReceiver","AnnaReceiver <init> is called");
        try {
            if(mAnnaMetas == null) {
                makeClazz();
            }

            if(mMethodGetMap == null) {
                mMethodGetMap = mAnnaMetas.getClass().getMethod("getMap");
            }

            mMap =  (HashMap<String, ArrayList<String>>)mMethodGetMap.invoke(mAnnaMetas);
//            for (Map.Entry<String,ArrayList<String>> entry : mMap.entrySet()) {
//                Log.d("annamap-test","key:    " + entry.getKey());
//                for(String configurationDO : entry.getValue()){
//                    Log.d("annamap-test",configurationDO.toString());
//                }
//            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    private ArrayList<IAnnaReceiver> getReceiver(String clazz, String methodName) throws Exception{
        ArrayList<IAnnaReceiver> list = new ArrayList<>();
        for (Map.Entry<String,ArrayList<String>> entry : mMap.entrySet()) {
            String[] s = entry.getKey().trim().split(" ");
            if(s[0].equals("**") || s[0].equals(clazz)){
                //所有接收
                if(Pattern.compile(s[1])
                        .matcher(methodName).matches()){
                    //匹配成功
                    if(entry.getValue() != null){
                        for(String v : entry.getValue()){
                            Log.d("annamap-test",v);
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

    public void onMethodExit(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.w("AnnaReceiver","onMethodExit is called:" + clazz + ";" + name);
        try {
            ArrayList<IAnnaReceiver> iAnnaReceivers = getReceiver(clazz, name);
            if(iAnnaReceivers != null && iAnnaReceivers.size() > 0){
                iAnnaReceivers.get(0).onMethodExit(clazz, obj, name, objects,rtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object onIntercept(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.w("AnnaReceiver","onIntercept is called:" + clazz + ";" + name);
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

    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype){
        Log.d("AssassinReveiver","onMethodEnter is called:" + clazz + ";" + name);
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
