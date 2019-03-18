package com.threathunter.labrador.common.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 */
public class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    public static String readFileAsString(String path) throws IOException {
        String content = "";
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            byte[] bytes = IOUtils.toByteArray(input);
            content = new String(bytes);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("", e);
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    public static HttpURLConnection getHttpURLConnection(String configPath) throws IOException {
        URL u = new URL(configPath);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("content-type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();
        return conn;
    }

    private static String readInputStream(InputStream in) throws IOException {
        char[] buffer = new char[2000];
        StringBuilder result = new StringBuilder();
        InputStreamReader ins = new InputStreamReader(in, "UTF-8");
        int readBytes;
        while ((readBytes = ins.read(buffer, 0, 2000)) >= 0) {
            result.append(buffer, 0, readBytes);
        }
        return result.toString();
    }


    public static String getUrlContent(String configPath) throws IOException {
        InputStream inputStream = null;
        String content = "";
        try {
            HttpURLConnection conn = getHttpURLConnection(configPath);
            inputStream = conn.getInputStream();
            content = readInputStream(inputStream);
            return content;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("", e);
                    e.printStackTrace();
                }
            }
        }
    }



    public static void main(String[] args) throws IOException {
//        System.out.println(getUrlContent("http://172.16.10.241:9001/platform/event_models?auth=feb2d59522d9794e065cf1bb0a6f53d0"));
    }
}
