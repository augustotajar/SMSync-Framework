package com.server.augusto.smserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GuardarConfiguracion extends Activity {

    DatabaseHelper db;
    String[] config;
    EditText inputDomain;
    EditText inputLocalServerIP;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        db = new DatabaseHelper(getApplicationContext());

        // Edit Text
        inputDomain = (EditText) findViewById(R.id.inputDomain);
        inputLocalServerIP = (EditText) findViewById(R.id.inputLocalServerIP);

        config = db.getConfig();
        if(config[0] != null || config[1] != null){
            inputDomain.setText(config[0]);
            inputLocalServerIP.setText(config[1]);
        }
        db.closeDB();
        // Create button
        Button btnSave = (Button) findViewById(R.id.btnSave);

        // button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db = new DatabaseHelper(getApplicationContext());
                String domain = inputDomain.getText().toString();
                String localServerIP = inputLocalServerIP.getText().toString();

                db.SaveConfig(domain,localServerIP);
                db.closeDB();
                // successfully saved configuration
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                // closing this screen
                finish();
            }
        });
    }

}