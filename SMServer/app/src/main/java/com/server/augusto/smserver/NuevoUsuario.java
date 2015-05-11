package com.server.augusto.smserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NuevoUsuario extends Activity {
    // Database Helper
    DatabaseHelper db;
    EditText inputNombre;
    EditText inputNumeroTelefono;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_usuario);

        // Edit Text
        inputNombre = (EditText) findViewById(R.id.inputNombre);
        inputNumeroTelefono = (EditText) findViewById(R.id.inputNumeroTelefono);

        // Create button
        Button btnCrearUsuario = (Button) findViewById(R.id.NuevoUsuario_boton);

        // button click event
        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db = new DatabaseHelper(getApplicationContext());
                String nombre = inputNombre.getText().toString();
                String telefono = inputNumeroTelefono.getText().toString();

                User user = new User(nombre,telefono);
                long user_id = db.createUser(user);
                db.closeDB();

                // successfully created user
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                // closing this screen
                finish();
            }
        });
    }
}
