package com.example.augusto.clientapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NuevoProducto extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputCode;
    EditText inputName;
    EditText inputPrice;
    EditText inputQuantity;

    // url to create new product
    private static String url_create_product = "http://192.168.0.150/androidconnect/CRUD/create.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_producto);

        // Edit Text
        inputCode = (EditText) findViewById(R.id.inputCode);
        inputName = (EditText) findViewById(R.id.inputName);
        inputPrice = (EditText) findViewById(R.id.inputPrice);
        inputQuantity = (EditText) findViewById(R.id.inputQuantity);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.NuevoProducto_boton);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewProduct().execute();
            }
        });
    }


    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NuevoProducto.this);
            pDialog.setMessage("Creando Producto..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String code = inputCode.getText().toString();
            String name = inputName.getText().toString();
            String price = inputPrice.getText().toString();
            String quantity = inputQuantity.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("price", price));
            params.add(new BasicNameValuePair("quantity", quantity));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(getApplicationContext(),url_create_product,
                    "POST", params,true);

            // check log for response
            Log.d("Creando respuesta", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), TodosLosProductos.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product, sending request trough SMS
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