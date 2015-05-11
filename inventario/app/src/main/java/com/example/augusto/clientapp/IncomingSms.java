package com.example.augusto.clientapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by augusto on 06/03/2015.
 */
public class IncomingSms extends BroadcastReceiver {

    DatabaseHelper db;
    JSONObject jObj = null;
    private static final String ServerNumber = "04264336862";
    private static final String CodePlusServerNumber = "+584264336862";
    private static final String prefix = "444C";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_API = "api";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ID = "id";
    private static final String TAG_CODE = "code";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_QUANTITY = "quantity";

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, String> msg = RetrieveMessages(intent);
        db = new DatabaseHelper(context);

        if (msg != null) {
            for (String sender : msg.keySet()) {
                String message = msg.get(sender);
                Log.i("SmsReceiver","Sender: " + sender + " Message: " + message);
                if((sender.equals(ServerNumber) || sender.equals(CodePlusServerNumber) )&& message.startsWith(prefix)){
                    message = message.substring(5);
                }
                try {
                    jObj = new JSONObject(message);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                try {
                    manageJsonResponse(jObj,context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        db.closeDB();
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

    public void manageJsonResponse(JSONObject response, Context context) throws JSONException {
        //Getting where is the json sms coming from
        switch (response.getInt(TAG_API)){
            //read_one
            case 1:
                // check your log for json response
                Log.d("manageJsonResponse", "Detalles del producto: " + response.toString());

                // json success tag
                int success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productArray = response
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    JSONObject product = productArray.getJSONObject(0);

                    int id = product.getInt(TAG_ID);
                    String code = product.getString(TAG_CODE);
                    String name = product.getString(TAG_NAME);
                    float price = Float.parseFloat(product.getString(TAG_PRICE));
                    int quantity = product.getInt(TAG_QUANTITY);

                    Product productObj = new Product(id,code,name,price,quantity);

                    int log = db.updateProduct(productObj);
                    if(log !=0){
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,"Informacion de " + name + " actualizada", duration);
                        toast.show();
                    }
                }else if (success == 0) {
                    // check your log for json response
                    Log.d("manageJsonResponse",response.toString());
                        // successfully delete product
                        db.deleteProduct(response.getLong(TAG_PRODUCT));
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,"Producto eliminado", duration);
                        toast.show();
                }

                break;
            //read_all
            case 2:
                //JSONArray products = null;
                Log.d("Todos los Productos: ", response.toString());

                /*try {
                    // Checking for SUCCESS TAG
                    success = response.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = response.getJSONArray(TAG_PRODUCTS);

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String code = c.getString(TAG_CODE);
                            String name = c.getString(TAG_NAME);
                            String price = c.getString(TAG_PRICE);
                            String quantity = c.getString(TAG_QUANTITY);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                break;
            //update
            case 3:
                // check your log for json response
                Log.d("manageJsonResponse", "Detalles del producto: " + response.toString());

                // json success tag
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully updated product
                    JSONArray productArray = response
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    JSONObject product = productArray.getJSONObject(0);

                    int id = product.getInt(TAG_ID);
                    String code = product.getString(TAG_CODE);
                    String name = product.getString(TAG_NAME);
                    float price = Float.parseFloat(product.getString(TAG_PRICE));
                    int quantity = product.getInt(TAG_QUANTITY);

                    Product productObj = new Product(id,code,name,price,quantity);

                    int log = db.updateProduct(productObj);
                    if(log !=0){
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,"Producto " + name + " actualizado", duration);
                        toast.show();
                    }
                }
                break;
            //create
            case 4:
                // check your log for json response
                Log.d("manageJsonResponse", "Detalles del producto: " + response.toString());

                // json success tag
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully updated product
                    JSONArray productArray = response
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    JSONObject product = productArray.getJSONObject(0);

                    int id = product.getInt(TAG_ID);
                    String code = product.getString(TAG_CODE);
                    String name = product.getString(TAG_NAME);
                    float price = Float.parseFloat(product.getString(TAG_PRICE));
                    int quantity = product.getInt(TAG_QUANTITY);

                    Product productObj = new Product(id,code,name,price,quantity);

                    long log = db.createProduct(productObj);
                    Log.d("manageJsonResponse", "ID del producto: " +  Long.toString(log));
                    if(log !=0){
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,"Producto " + name + " creado", duration);
                        toast.show();
                    }
                }
                break;
            //delete
            case 5:
                // check your log for json response
                Log.d("manageJsonResponse",response.toString());

                // json success tag
                success = response.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully delete product
                    db.deleteProduct(response.getLong(TAG_PRODUCT));
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,"Producto eliminado", duration);
                    toast.show();
                }
                break;
            default:
                Log.e("manageJsonResponse", "api invalido");
                break;
        }
    }
}