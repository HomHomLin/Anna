package com.meetyou.aop.demo;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.meetyou.anna.client.impl.IAnnaReceiver;
import com.meetyou.anna.plugin.AnnaReceiver;

/**
 * Created by Linhh on 17/6/14.
 */
@AnnaReceiver("** onClick")
public class TestReceiver extends IAnnaReceiver{

    @Override
    public boolean onMethodEnter(String clazz, Object obj, String name, Object[] objects, String rtype) {
        Log.d("TestReceiver", "onMethodEnter:" + clazz + ";" + name);
        if(obj != null &&
                objects != null &&
                objects.length == 1 &&
                objects[0] != null){
            Object obj_item = objects[0];
            //null是静态方法,不是我想要的点击事件,因为listener回调只有一个参数,所以如果不是1个的不考虑在内
            if(obj_item instanceof View){
                View view = (View)obj_item;
                Log.d("TestReceiver", "this is a : view --->" + view + "," + view.getContext());
//
//                    Context context = view.getContext();
//                    if()
            }
        }
        return super.onMethodEnter(clazz, obj, name, objects, rtype);
    }
}
