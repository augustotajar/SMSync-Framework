package com.example.augusto.clientapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Log tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SMSFrameworkDB";

    // Table Name
    private static final String TABLE_PRODUCTS = "products";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_UPDATED_AT = "updated_at";

    // Table Create Statements
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
            + TABLE_PRODUCTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CODE
            + " TEXT," + KEY_NAME + " TEXT," +KEY_PRICE + " REAL," +KEY_QUANTITY
            + " INTEGER,"+ KEY_UPDATED_AT + " DATETIME" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PRODUCT);

        // create new tables
        onCreate(db);
    }

    //********************************************************************************************
    public long createProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(product.getId() != 0){
            values.put(KEY_ID, product.getId());
        }
        values.put(KEY_CODE, product.getCode());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_QUANTITY, product.getQuantity());

        // insert row
        long product_id = db.insert(TABLE_PRODUCTS, null, values);

        return product_id;
    }

    public Product getProduct_byId(long product_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE "
                + KEY_ID + " = " + product_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Product product = new Product();
        product.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        product.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
        product.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        product.setPrice((c.getFloat(c.getColumnIndex(KEY_PRICE))));
        product.setQuantity((c.getInt(c.getColumnIndex(KEY_QUANTITY))));

        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            Log.d("getAllProducts",Integer.toString(c.getCount()) + " products");
            do {
                Product product = new Product();
                product.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                product.setCode((c.getString(c.getColumnIndex(KEY_CODE))));
                product.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                product.setPrice((c.getFloat(c.getColumnIndex(KEY_PRICE))));
                product.setQuantity((c.getInt(c.getColumnIndex(KEY_QUANTITY))));
                products.add(product);
            } while (c.moveToNext());
        }

        return products;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, product.getCode());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_QUANTITY, product.getQuantity());

        // updating row
        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(product.getId()) });
    }

    public void updateAll(ArrayList<HashMap<String, String>> productsList) {
        List<Integer> productsIds = new ArrayList<>();
        for(HashMap<String, String> hashMap: productsList) {
            Product product = new Product();
            productsIds.add(Integer.decode(hashMap.get(KEY_ID)));
            product.setId(Integer.decode(hashMap.get(KEY_ID)));
            product.setCode(hashMap.get(KEY_CODE));
            product.setName(hashMap.get(KEY_NAME));
            product.setPrice(Float.parseFloat(hashMap.get(KEY_PRICE)));
            product.setQuantity(Integer.decode(hashMap.get(KEY_QUANTITY)));
            if(updateProduct(product) == 0) {
                createProduct(product);
            }
        }
        List<Product> ProductsToDelete = getAllProducts();
        while (!productsIds.isEmpty()){
            boolean Band=false;
            int i=0;
            while(!Band || i<ProductsToDelete.size())
            {
                if(ProductsToDelete.get(i).getId() == productsIds.get(0)){
                    ProductsToDelete.remove(i);
                    Band = true;
                }
                i++;
            }
            productsIds.remove(0);
        }
        for (Product p: ProductsToDelete){
            deleteProduct(p.getId());
        }
    }

    public void deleteProduct(long product_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[] { String.valueOf(product_id) });
    }

    public int getProductCount() {
        String countQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}