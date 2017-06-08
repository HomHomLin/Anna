package com.meetyou.anna.plugin;

/**
 * Created by Linhh on 17/5/31.
 */
@AntiAssassin
public class AssassinDO {
    public String name;
    public String des;//匹配类型,all还是normal

    @Override
    public boolean equals(Object o) {
        AssassinDO o1 = (AssassinDO)o;
        return o1.name.contains(this.name) && o1.des.equals(des);
    }

    @Override
    public String toString() {
        return "name:" + name + ",des:" + des;
    }
}
