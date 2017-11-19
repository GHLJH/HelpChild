package com.le.help_child.util;

import com.le.help_child.activity.TabActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class NetTool {
    public static boolean ifcodetrue = false;

    //通过post向服务器端发送数据，并获得服务器端输出流
    public static InputStream getInputStreamByPost(String urlPath, Map<String, String> params, String encoding) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }
        String data = sb.deleteCharAt(sb.length() - 1).toString();
        URL url = new URL(urlPath);
        //打开连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置提交方式
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        //post方式不能使用缓存
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        //设置连接超时时间
        conn.setConnectTimeout(6 * 1000);
        //配置本次连接的Content-Type，配置为application/x-www-form-urlencoded
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // 维持长连接
        conn.setRequestProperty("Connection", "Keep-Alive");
        // 设置浏览器编码
        conn.setRequestProperty("Charset", "UTF-8");
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        // 将请求参数数据向服务器端发送
        dos.writeBytes(data);
        dos.flush();
        dos.close();
        int res = conn.getResponseCode();
        if (res == 200) {
            ifcodetrue = true;
            // 获得服务器端输出流
            return conn.getInputStream();
        }
        return null;
    }

    // 通过输入流获得字节数组
    public static byte[] readStream(InputStream is) throws Exception {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        is.close();
        return bos.toByteArray();
    }

}
