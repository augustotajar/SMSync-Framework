package com.server.augusto.smserver;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button Config_boton;
    Button TodosLosUsuarios_boton;
    Button NuevoUsuario_boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Buttons
        Config_boton = (Button) findViewById(R.id.ConfigurarServidor);
        TodosLosUsuarios_boton = (Button) findViewById(R.id.TodosLosUsuarios_boton);
        NuevoUsuario_boton = (Button) findViewById(R.id.NuevoUsuario_boton);

        // view users click event
        Config_boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching All users Activity
                Intent i = new Intent(getApplicationContext(), GuardarConfiguracion.class);
                startActivity(i);
            }
        });

        // view users click event
        TodosLosUsuarios_boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching All users Activity
                Intent i = new Intent(getApplicationContext(), TodosLosUsuarios.class);
                startActivity(i);
            }
        });

        // view users click event
        NuevoUsuario_boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching create new user activity
                Intent i = new Intent(getApplicationContext(), NuevoUsuario.class);
                startActivity(i);
            }
        });

    }
}