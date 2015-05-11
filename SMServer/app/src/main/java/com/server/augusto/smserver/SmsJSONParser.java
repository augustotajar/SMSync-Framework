package com.server.augusto.smserver;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SmsJSONParser {

    InputStream is = null;
    JSONObject jObj = null;
    String json = "";
    String url;
    String method;
    List<NameValuePair> params = new ArrayList<>();
    DatabaseHelper db;

    // constructor
    public SmsJSONParser() {

    }

    public JSONObject makeHttpRequestFromSms(String sender, String msj,Context context) {
        db = new DatabaseHelper(context);
        if(db.validateUserbyPhone(sender)){
            String[] parts = msj.split(";");
            url = parts[0];

            //Changing domain to local ip address
            String[] config = db.getConfig();
            if(config[0] != null || config[1] != null){
                url = url.replace(config[0],config[1]);
                Log.d("SmsJSONParser", config[0] + " " + config[1]);
            }

            method = parts[1];

            if(parts.length > 2){
                String[] paramsStrings = parts[2].split(",");
                for (String paramsString : paramsStrings) {
                    if (!paramsString.equals("")) {
                        String[] nameValuePair = paramsString.split(":");
                        if(nameValuePair.length > 1) {
                            String name = nameValuePair[0];
                            String value = nameValuePair[1];
                            params.add(new BasicNameValuePair(name, value));
                        }
                    }
                }
            }

            Log.d("SmsJSONParser", url);
            Log.d("SmsJSONParser", method);
            try {
                if(method.equals("POST")){
                    Log.d("SmsJSONParser", "POST");
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();

                }else if(method.equals("GET")){
                    Log.d("SmsJSONParser", "GET");
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }

            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                Log.d("SmsJSONParser", "UnsupportedEncodingException ClientProtocolException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("SmsJSONParser", "IOException");
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            db.closeDB();
            return jObj;
        }else {
            Log.e("Usario invalido", sender + " No puede consultar la BD");
            try {
                jObj = new JSONObject("{\"success\":0,\"message\": \"Connection Error\"}");
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            db.closeDB();
            return jObj;
        }
    }
}