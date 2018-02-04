package com.mail.pengaduanmail.app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by fandis on 09/01/2018.
 */

public class JSONHandler {
    String charset = "UTF-8";
    HttpURLConnection connection;
    DataOutputStream wr;
    StringBuilder result;
    URL urlAdd;
    StringBuilder sbParams;
    String paramString;

    static JSONObject jObj = null;
    static String json = "";

    public JSONHandler() {
    }

    public String makeHttpRequest(String url, String method, HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }

        if (method.equalsIgnoreCase("POST")) {
            try {
                urlAdd = new URL(url);
                connection = (HttpURLConnection) urlAdd.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);

                connection.connect();

                paramString = sbParams.toString();

                wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(paramString);
                wr.flush();
                wr.close();
                Log.d("URL", url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equalsIgnoreCase("GET")) {
            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
                Log.d("URL", url);
            }

            try {
                urlAdd = new URL(url);
                connection = (HttpURLConnection) urlAdd.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.disconnect();

        return result.toString();
    }

    public JSONObject showContent() {
        try {
            jObj = new JSONObject(json);
        }catch (JSONException e){
            Log.e("JSOn Parser", "Error parsing data" + e.toString());
        }
        return jObj;
    }
}
