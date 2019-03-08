package com.threathunter.labrador.common.util;

import com.threathunter.config.CommonDynamicConfig;

/**
 * Created by wanbaowang on 17/8/22.
 */
public class ConfigUtil {
    private static class ConfigUtilHolder {
        private static CommonDynamicConfig commonDynamicConfig = CommonDynamicConfig.getInstance();

        static {
            commonDynamicConfig.addConfigFiles("nebula.conf", "labrador.properties");
        }

        public static CommonDynamicConfig getConfig() {
            return commonDynamicConfig;
        }
    }

    public static CommonDynamicConfig getConfig() {
        return ConfigUtilHolder.getConfig();
    }

    public static void addFile(String fileName) {
        ConfigUtilHolder.commonDynamicConfig.addConfigFile(fileName);
    }

    public static String getString(String key) {
        CommonDynamicConfig config = ConfigUtilHolder.getConfig();
        return config.getString(key);
    }

    public static boolean contains(String key) {
        CommonDynamicConfig config = ConfigUtilHolder.getConfig();
        return config.containsKey(key);
    }

    public static int getInt(String key) {
        CommonDynamicConfig config = ConfigUtilHolder.getConfig();
        return config.getInt(key);
    }

    public static int getIntDefault(String key, int value) {
        CommonDynamicConfig config = ConfigUtilHolder.getConfig();
        return config.containsKey(key) ? config.getInt(key) : value;
    }

    public static void main(String[] args) {
        System.out.println(ConfigUtil.getString("babel_server"));
    }
}
