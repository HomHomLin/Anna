package com.meetyou.anna;

/**
 * Created by Linhh on 17/6/13.
 */

public class ConfigurationDO {
    public String[] strings;

    @Override
    public String toString() {
        String s = "";
        for(String s1: strings){
            s = s + s1 + "  ";
        }
        return s;
    }
}
