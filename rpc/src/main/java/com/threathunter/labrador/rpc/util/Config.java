package com.threathunter.labrador.rpc.util;


import java.util.ResourceBundle;

/**
 * 
 */
public class Config {
    private static ResourceBundle bundle;
    static {
        bundle = ResourceBundle.getBundle("rpc");
    }

    public static String getString(String key){
        return bundle.getString(key);
    }

    public static int getInt(String key){
        return Integer.parseInt(getString(key));
    }

}
