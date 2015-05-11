package com.example.augusto.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantallaPrincipal extends Activity{

    Button TodosLosProductos_boton;
    Button NuevoProducto_boton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);

        // Buttons
        TodosLosProductos_boton = (Button) findViewById(R.id.TodosLosProductos_boton);
        NuevoProducto_boton = (Button) findViewById(R.id.NuevoProducto_boton);

        // view products click event
        TodosLosProductos_boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), TodosLosProductos.class);
                startActivity(i);

            }
        });

        // view products click event
        NuevoProducto_boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NuevoProducto.class);
                startActivity(i);

            }
        });
    }
}