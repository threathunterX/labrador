package com.threathunter.labrador.common.util;

/**
 * 
 */
public class DimensionUtil {


    public static boolean isSameDimentsion(String field, String dimension) {
        if(field.equals("c_ip")) {
            return "ip".equals(dimension);
        } else {
            return field.equals(dimension);
        }
    }

}
