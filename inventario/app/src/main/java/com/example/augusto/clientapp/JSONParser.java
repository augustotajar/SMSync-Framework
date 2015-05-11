package com.example.augusto.clientapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.telephony.SmsManager;

public class JSONParser {

    InputStream is = null;
    JSONObject jObj = null;
    SmsManager smsM = SmsManager.getDefault();
    String json = "";
    ArrayList jsonStringMsj = null;
    private static final String ServerNumber = "+584264336862";

    // constructor
    public JSONParser() {

    }

    public JSONObject makeHttpRequest(Context context, String url, String method,
                                      List<NameValuePair> params, boolean sms) {

        ConnectionDetector cd = new ConnectionDetector(context);
        if(cd.isConnectingToInternet()){
            // Making HTTP request
            try {
                if(method == "POST"){
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();

                }else if(method == "GET"){
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                //Send request trough SMS
                return SendSMS(url,method,params,sms);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
                //Send request trough SMS
                return SendSMS(url,method,params,sms);
            }

            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return jObj;
        }else{
            //Send request trough SMS
            return SendSMS(url,method,params,sms);
        }
    }
    public JSONObject SendSMS(String url, String method,List<NameValuePair> params,boolean sms) {
        if(sms){
            String P ="";
            for (NameValuePair nameValuePair : params) {
                P += nameValuePair.getName();
                P += ":";
                P += nameValuePair.getValue();
                P += ",";
            }
            jsonStringMsj = smsM.divideMessage(url+";"+method+";"+P);
            smsM.sendMultipartTextMessage(ServerNumber, null,jsonStringMsj, null, null);

            json = "{\"success\":-1,\"message\": \"Connection Error, SMS with request send to server\"}";
        }else {
            // return JSON String
            json = "{\"success\":-1,\"message\": \"Connection Error\"}";
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }
}