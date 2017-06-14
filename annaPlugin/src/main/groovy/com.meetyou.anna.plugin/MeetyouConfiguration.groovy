package com.meetyou.anna.plugin

import com.meetyou.anna.ConfigurationDO;

/**
 * 配置信息插件
 * Created by Linhh on 17/6/13.
 */

public class MeetyouConfiguration {
    private final String mFileName;
    HashMap<String,ArrayList<ConfigurationDO>> map = new HashMap<>();
    public MeetyouConfiguration(String fileName){
        mFileName = fileName;
    }

    public void print(){
        for (Map.Entry<String,ArrayList<ConfigurationDO>> entry : map.entrySet()) {
            println "configuration do:"
            System.out.println(entry.key);
            for(ConfigurationDO configurationDO : entry.value){
                System.out.println(configurationDO.toString());
            }
        }
    }

    public HashMap<String,ArrayList<ConfigurationDO>> getMap(){
        return map;
    }

    public void process() throws Exception{
        String configuration_name = null;
        int line_index = 0;
        new File(mFileName).eachLine {
            line_index ++;
            if(it != null && !it.trim().startsWith("#") && !it.trim().empty) {
                //内容
                it = it.trim();
                if(it.startsWith("-") && it.endsWith("{")){
                    //起始
                    configuration_name = it.substring(1, it.length() - 1).trim();
                    map.put(configuration_name, null);
                }else if(configuration_name != null &&
                        it.endsWith("}") &&
                        !configuration_name.empty){
                    //结束
                    configuration_name = null;
                }else if(configuration_name != null &&
                        it.endsWith(";")&&
                        !configuration_name.empty){
                    //真实语句
                    it = it.substring(0, it.length() - 1).trim();
                    ArrayList<ConfigurationDO> list = map.get(configuration_name);
                    if(list == null){
                        list = new ArrayList<>();
                    }
                    String[] strings = it.split(" ");
                    for(int i = 0; i < strings.length; i ++){
                        strings[i] = strings[i].replace("[/space*]", " ");
                    }
                    ConfigurationDO configurationDO = new ConfigurationDO();
                    configurationDO.strings = strings;
                    list.add(configurationDO);
                    map.put(configuration_name, list);
                }else{
                    //出错行
                    throw new Exception("Meetyou Configuration is error, file :" + mFileName + ",line: " + line_index);
                }
            }else{
                //注释,空行
            }
        }
    }
}
