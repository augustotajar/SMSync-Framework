package com.server.augusto.smserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IncomingSms extends BroadcastReceiver {

    private String prefix = "444C ";
    SmsManager smsM = SmsManager.getDefault();
    ArrayList jsonStringMsj = null;
    SmsJSONParser JParser;
    JSONObject json;

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, String> msg = RetrieveMessages(intent);
        if (msg != null) {
            for (String sender : msg.keySet()) {
                String message = msg.get(sender);
                JParser = new SmsJSONParser();
                Log.i("IncomingSms","Sender: " + sender + " Message: " + message);
                json = JParser.makeHttpRequestFromSms(sender, message, context);
                Log.d("JsonString Response",json.toString());

                //Send Json to client
                jsonStringMsj = smsM.divideMessage(prefix + json.toString());
                smsM.sendMultipartTextMessage(sender, null,jsonStringMsj, null, null);
            }
        }
    }

    private static Map<String, String> RetrieveMessages(Intent intent) {
        Map<String, String> msg = null;
        SmsMessage[] msgs;
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                    String originatinAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatinAddress)) {
                        // Index with number doesn't exist
                        // Save string into associative array with sender number as index
                        msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());

                    } else {
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatinAddress);
                        String msgString = previousparts + msgs[i].getMessageBody();
                        msg.put(originatinAddress, msgString);
                    }
                }
            }
        }

        return msg;
    }
}