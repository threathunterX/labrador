package com.threathunter.labrador;

import com.threathunter.labrador.core.env.Env;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 
 */
public class AbstractTest {

    static {
        try {
            Env.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static char[] chs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e'};
    private static List<String> ips = Arrays.asList("1.2.3.4", "1.2.3.5", "1.2.3.6", "1.2.3.7", "1.2.3.8", "1.2.3.9", "1.2.3.10", "1.2.3.11", "1.2.3.12", "1.2.3.13", "1.2.3.14", "1.2.3.15");


    public static String randomId() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 24; i++) {
            int randomInt = r.nextInt(14);
            sb.append(chs[randomInt]);
        }
        return sb.toString();
    }

    public static String randomIp() {
        Random r = new Random();
        int randomInt = r.nextInt(ips.size());
        return ips.get(randomInt);
    }


}
