package com.server.augusto.smserver;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TodosLosUsuarios extends ListActivity{

    // Database Helper
    DatabaseHelper db;
    ArrayList<HashMap<String, String>> usersList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todos_los_usuarios);

        db = new DatabaseHelper(getApplicationContext());
        // Hashmap for ListView
        usersList = new ArrayList<HashMap<String, String>>();
        List<User> allUsers = db.getAllUsers();

        if (allUsers.isEmpty()) {
            Intent i = new Intent(getApplicationContext(),
                    NuevoUsuario.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            for (User user : allUsers) {
                Log.d("Contacto:", user.getName() +": "+ user.getPhone_number());
                String id = Long.toString(user.getId());
                String name = user.getName();
                String phone = user.getPhone_number();

                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("id", id);
                map.put("name", name);
                map.put("phone_number", phone);

                usersList.add(map);
            }
        }

        ListAdapter adapter = new SimpleAdapter(
                TodosLosUsuarios.this, usersList,
                R.layout.usuario_lista, new String[] { "id",
                "name","phone_number"},
                new int[] { R.id.id, R.id.name,R.id.phone });
        // updating listview
        setListAdapter(adapter);

        ListView lv = getListView();

        // on selecting single user
        // launching Edit user Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String uid = ((TextView) view.findViewById(R.id.id)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditarUsuario.class);
                // sending id to next activity
                in.putExtra("id", uid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

        db.closeDB();

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted user
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

}
