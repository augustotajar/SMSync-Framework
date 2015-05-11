package com.server.augusto.smserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditarUsuario extends Activity {

    EditText txtName;
    EditText txtPhone;
    Button btnSave;
    Button btnDelete;
    // Database Helper
    DatabaseHelper db;

    String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_usuario);

        db = new DatabaseHelper(getApplicationContext());
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting product details from intent
        Intent i = getIntent();
        // getting product id from intent
        uid = i.getStringExtra("id");

        User user = db.getUser(Integer.parseInt(uid));
        // Edit Text
        txtName = (EditText) findViewById(R.id.inputName);
        txtPhone = (EditText) findViewById(R.id.inputPhone);

        // display user data in EditText
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhone_number());

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update user
               SaveUserDetails();
            }


        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                db.deleteUser(Integer.parseInt(uid));
                Intent i = getIntent();
                setResult(100, i);
                finish();
            }
        });

    }

    private void SaveUserDetails() {
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();

        User user = new User(Integer.parseInt(uid),name,phone);
        db.updateUser(user);
        Intent i = getIntent();
        setResult(100, i);
        finish();
    }

}
