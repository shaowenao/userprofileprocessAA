package com.swa.upp.utils;

import java.util.ResourceBundle;

public class PropertiesUtil
{
   //传入要读取文件的名字不用传入后缀就可读取
    static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

    public static String getValue(String key){

           return resourceBundle.getString(key);

    }

    public static void main(String[] args) {

        System.out.println(getValue("masterUrl"));

    }
}
