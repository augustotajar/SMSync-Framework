package com.server.augusto.smserver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SMServer";

    // Table Name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CONFIG = "config";

    // Common column names
    //user
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE_NUM = "phone_number";
    private static final String KEY_CREATED_AT = "created_at";

    //config
    private static final String DOMAIN_NAME = "domain_name";
    private static final String LOCAL_IP_ADDRESS = "local_ip_address";

    // Table Create Statements
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_PHONE_NUM + " TEXT" + ")";

    // Table Create Statements
    private static final String CREATE_TABLE_CONFIG = "CREATE TABLE "
            + TABLE_CONFIG + "(" + KEY_ID + " INTEGER PRIMARY KEY," + DOMAIN_NAME
            + " TEXT," + LOCAL_IP_ADDRESS+ " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CONFIG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CONFIG);

        // create new tables
        onCreate(db);
    }

//********************************************************************************************
    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PHONE_NUM, user.getPhone_number());

        // insert row
        long user_id = db.insert(TABLE_USERS, null, values);

        return user_id;
    }

    public User getUser(long user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_ID + " = " + user_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        User user = new User();
        user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        user.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        user.setPhone_number((c.getString(c.getColumnIndex(KEY_PHONE_NUM))));

        return user;
    }

    public boolean validateUserbyPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_PHONE_NUM + " = " +"'"+ phone +"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            if (c.moveToFirst()) {
                Log.d("validateUserbyPhone","TRUE");
                return true;
            }else {
                Log.d("validateUserbyPhone","FALSE");
                return false;
            }
        }else
            Log.d("validateUserbyPhone","FALSE");
            return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            Log.d("getAllUsers","c="+ Integer.toString(c.getCount()));
            do {
                User user = new User();
                user.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                user.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                user.setPhone_number((c.getString(c.getColumnIndex(KEY_PHONE_NUM))));

                users.add(user);
            } while (c.moveToNext());
        }

        return users;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PHONE_NUM, user.getPhone_number());

        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public void deleteUser(long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] { String.valueOf(user_id) });
    }

    public int getUserCount() {
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public long SaveConfig(String domain, String local_ip_address) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_CONFIG);
        ContentValues values = new ContentValues();
        values.put(DOMAIN_NAME, domain);
        values.put(LOCAL_IP_ADDRESS, local_ip_address);

        // insert row
        long config_id = db.insert(TABLE_CONFIG, null, values);

        return config_id;
    }

    public String[] getConfig() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] config = new String[2];
        config[0] = null;
        config[1] = null;
        String selectQuery = "SELECT * FROM " + TABLE_CONFIG ;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
            if(c.getCount() > 0){
                config[0] = c.getString(c.getColumnIndex(DOMAIN_NAME));
                config[1] = c.getString(c.getColumnIndex(LOCAL_IP_ADDRESS));
            }
        }
        return config;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}