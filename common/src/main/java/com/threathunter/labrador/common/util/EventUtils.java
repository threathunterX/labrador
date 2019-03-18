package com.threathunter.labrador.common.util;

import com.threathunter.model.Event;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 
 */
public class EventUtils {
    public static String getEventDimensionValue(Event event, String dimension) {
        String key = "";
        if (dimension.equals("user")) {
            key = (String) event.getPropertyValues().get("uid");
        } else if (dimension.equals("ip")) {
            key = (String) event.getPropertyValues().get("c_ip");
        } else if (dimension.equals("did")) {
            key = (String) event.getPropertyValues().get("did");
        } else if (dimension.equals("c_ip")) {
            key = (String) event.getPropertyValues().get("c_ip");
        } else if (dimension.equals("uid")) {
            key = (String) event.getPropertyValues().get("uid");
        } else if (dimension.equals("page")) {
            key = (String) event.getPropertyValues().get("page");
            key = calMd5(key);
        }
        return key;
    }

    private static char[] chs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e'};

    public static String randomId() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 24; i++) {
            int randomInt = r.nextInt(14);
            sb.append(chs[randomInt]);
        }
        return sb.toString();
    }

    private static String calMd5(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try {
                md.update(msg.getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer(200);
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset] & 0xff;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            return msg;
        }
    }
}
