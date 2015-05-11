package com.example.augusto.clientapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TodosLosProductos extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    int success;
    ArrayList<HashMap<String, String>> productsList;
    DatabaseHelper db;

    // url to get all products list
    private static String url_all_products = "http://192.168.0.150/androidconnect/CRUD/readAll.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ID = "id";
    private static final String TAG_CODE = "code";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_QUANTITY = "quantity";

    JSONArray products = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todos_los_productos);

        // Hash map to update ListView
        productsList = new ArrayList<HashMap<String, String>>();
        db = new DatabaseHelper(this.getApplicationContext());

        // Loading products in Background Thread
        new CargarProductos().execute();

        // Get list view
        ListView lv = getListView();

        // on selecting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.id)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditarProducto.class);
                // sending id to next activity
                in.putExtra(TAG_ID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    class CargarProductos extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TodosLosProductos.this);
            pDialog.setMessage("Cargando Productos, por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(getApplicationContext(),url_all_products,
                    "GET", params,false);

            // Check your log for JSON response
            Log.d("Todos los Productos: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String code = c.getString(TAG_CODE);
                        String name = c.getString(TAG_NAME);
                        String price = c.getString(TAG_PRICE);
                        String quantity = c.getString(TAG_QUANTITY);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, id);
                        map.put(TAG_CODE, code);
                        map.put(TAG_NAME, name);
                        map.put(TAG_PRICE, price);
                        map.put(TAG_QUANTITY, quantity);

                        // adding HashMap to ArrayList
                        productsList.add(map);
                    }
                } else if (success == 0){
                    Intent i = new Intent(getApplicationContext(),
                            NuevoProducto.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    //HTTP request fail, getting products from SQLite
                    List<Product> DbProductsList = db.getAllProducts();
                    for(Product p: DbProductsList){
                        String id = Long.toString(p.getId());
                        String code = p.getCode();
                        String name = p.getName();
                        String price = Float.toString(p.getPrice());
                        String quantity = Integer.toString(p.getQuantity());

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, id);
                        map.put(TAG_CODE, code);
                        map.put(TAG_NAME, name);
                        map.put(TAG_PRICE, price);
                        map.put(TAG_QUANTITY, quantity);

                        // adding HashMap to ArrayList
                        productsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            TodosLosProductos.this, productsList,
                            R.layout.producto_lista, new String[] {TAG_ID,
                            TAG_NAME, TAG_PRICE},
                            new int[] { R.id.id, R.id.name, R.id.price });
                    // dismiss the dialog after getting all products
                    pDialog.dismiss();
                    // updating listview
                    setListAdapter(adapter);
                    //Updating SQLite Database
                    if(success == 1) {
                        db.updateAll(productsList);
                    }
                    db.closeDB();
                }
            });

        }

    }
}