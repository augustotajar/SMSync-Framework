package com.example.augusto.clientapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditarProducto extends Activity {

    EditText txtCode;
    EditText txtName;
    EditText txtPrice;
    EditText txtQuantity;
    EditText txtCreatedAt;
    Button btnSave;
    Button btnDelete;

    DatabaseHelper db;
    String id;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_product_detials = "http://192.168.0.150/androidconnect/CRUD/readOne.php";

    // url to update product
    private static final String url_update_product = "http://192.168.0.150/androidconnect/CRUD/update.php";

    // url to delete product
    private static final String url_delete_product = "http://192.168.0.150/androidconnect/CRUD/delete.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_ID = "id";
    private static final String TAG_CODE = "code";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_QUANTITY = "quantity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_producto);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        //Obtaining DB
        db = new DatabaseHelper(this.getApplicationContext());

        // getting product details from intent
        Intent i = getIntent();
        id = i.getStringExtra(TAG_ID);

        // Getting complete product details in background thread
        new GetProductDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveProductDetails().execute();
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProduct().execute();
            }
        });

    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditarProducto.this);
            pDialog.setMessage("Cargando detalles del producto...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", id));

                        // getting product details by making HTTP request
                        JSONObject json = jsonParser.makeHttpRequest(getApplicationContext(),
                                url_product_detials, "GET", params,true);

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // Edit Text
                            txtCode = (EditText) findViewById(R.id.inputCode);
                            txtName = (EditText) findViewById(R.id.inputName);
                            txtPrice = (EditText) findViewById(R.id.inputPrice);
                            txtQuantity = (EditText) findViewById(R.id.inputQuantity);

                            // display product data in EditText
                            txtCode.setText(product.getString(TAG_CODE));
                            txtName.setText(product.getString(TAG_NAME));
                            txtPrice.setText(product.getString(TAG_PRICE));
                            txtQuantity.setText(product.getString(TAG_QUANTITY));

                        }else if (success == -1){
                            // HTTP request fail, getting product from SQLite and wait SMS to update
                            Product p = db.getProduct_byId(Long.parseLong(id));

                            String id = Long.toString(p.getId());
                            String code = p.getCode();
                            String name = p.getName();
                            String price = Float.toString(p.getPrice());
                            String quantity = Integer.toString(p.getQuantity());

                            // Edit Text
                            txtCode = (EditText) findViewById(R.id.inputCode);
                            txtName = (EditText) findViewById(R.id.inputName);
                            txtPrice = (EditText) findViewById(R.id.inputPrice);
                            txtQuantity = (EditText) findViewById(R.id.inputQuantity);

                            // display product data in EditText
                            txtCode.setText(code);
                            txtName.setText(name);
                            txtPrice.setText(price);
                            txtQuantity.setText(quantity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    class SaveProductDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditarProducto.this);
            pDialog.setMessage("Guardando cambios ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String code = txtCode.getText().toString();
            String name = txtName.getText().toString();
            String price = txtPrice.getText().toString();
            String quantity = txtQuantity.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_ID, id));
            params.add(new BasicNameValuePair(TAG_CODE, code));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_PRICE, price));
            params.add(new BasicNameValuePair(TAG_QUANTITY, quantity));

            // sending modified data through http request
            JSONObject json = jsonParser.makeHttpRequest(getApplicationContext(),url_update_product,
                    "POST", params,true);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product, sending request trough SMS
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }

    class DeleteProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditarProducto.this);
            pDialog.setMessage("Eliminando Producto...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", id));

                // deleting product by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(getApplicationContext(),
                        url_delete_product, "POST", params,true);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                } else {
                    // failed to delete product, sending request trough SMS
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

        }

    }
}