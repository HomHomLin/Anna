package com.meetyou.aop.demo;

import android.view.View;
import android.view.ViewParent;

/**
 * Created by Linhh on 17/6/15.
 */

public class ViewPathUtils {
    public static String createViewPath(View child_view){
        if(child_view == null){
            return null;
        }
        ViewParent view = child_view.getParent();
        do {
            
        }while ((view = view.getParent())instanceof View);//2. 将view指向上一级的节点
        return null;
    }
}
