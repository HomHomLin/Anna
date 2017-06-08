package com.meetyou.anna.client.impl;


import com.meetyou.anna.plugin.AntiAssassin;

/**
 * Created by Linhh on 17/6/1.
 */
@AntiAssassin
public abstract class IAssassinDelegate {

    public void onMethodEnd(Object obj, String name, Object[] objects, String rtype){

    }

    public Object onMethodEnter(Object obj, String name, Object[] objects, String rtype){
        return null;
    }
}
