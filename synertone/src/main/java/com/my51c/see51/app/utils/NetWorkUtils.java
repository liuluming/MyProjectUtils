package com.my51c.see51.app.utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkUtils {
    public static JSONObject getJson(String path) {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            if (200 == connection.getResponseCode()) {
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                // byte array[] = baos.toByteArray();
                JSONObject jsonObject = new JSONObject(baos.toString());
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
