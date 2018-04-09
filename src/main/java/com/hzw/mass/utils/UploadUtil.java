package com.hzw.mass.utils;

import org.apache.http.client.ClientProtocolException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright@www.localhost.com.
 * Author:H.zw
 * Date:2018/4/3 11:41
 * Description:
 */
public class UploadUtil {

    //上传文件
    public static String postFile(String accessToken, String filePath) {

        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken + "&type=image";
        File file = new File(filePath);
        if (!file.exists()){
            return null;
        }
        String result = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            String boundary = "-----------------------------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = conn.getOutputStream();
            output.write(("--" + boundary + "\r\n").getBytes());
            output.write(
                    String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", file.getName())
                            .getBytes());
            output.write("Content-Type: image/jpeg \r\n\r\n".getBytes());
            byte[] data = new byte[1024];
            int len = 0;
            FileInputStream input = new FileInputStream(file);
            while ((len = input.read(data)) > -1) {
                output.write(data, 0, len);
            }
            output.write(("\r\n--" + boundary + "\r\n\r\n").getBytes());
            output.flush();
            output.close();
            input.close();
            InputStream resp = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            while ((len = resp.read(data)) > -1){
                sb.append(new String(data, 0, len, "utf-8"));
            }
            resp.close();
            result = sb.toString();
            System.out.println(result);
        } catch (ClientProtocolException e) {
            throw new RuntimeException("postFile，不支持http协议", e);
        } catch (IOException e) {
            throw new RuntimeException("postFile数据传输失败", e);
        }
        System.out.println(result);
        return result;
    }
}
