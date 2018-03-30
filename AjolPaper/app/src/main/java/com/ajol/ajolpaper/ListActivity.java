package com.ajol.ajolpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson; //Owen: see app/build.gradle: I added this library to convert objects to JSON

import java.util.ArrayList;

/**
 * Created by owengallagher on 3/29/18.
 */

public class ListActivity extends AppCompatActivity {
    ArrayList<Wallpaper> wallpapers = new ArrayList<>(0);
    ArrayAdapter<Wallpaper> wallpapersArrayAdapter;
    ListView wallpapersView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //read wallpapers in from database to fill wallpapers[]

        //link wallpapersView to wallpapers
        wallpapersArrayAdapter = new ArrayAdapter<Wallpaper>(getApplicationContext(),R.layout.list_item);
        wallpapersView = findViewById(R.id.wallpapers_view);
        wallpapersView.setAdapter(wallpapersArrayAdapter);

        //make the list items clickable (you don't have to press on its edit button)
        wallpapersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //I assume each wallpaper in the list view will correspond to the wallpaper in the array list at the same index
                Wallpaper selected = wallpapers.get(position);

                //create intent to start ModifyActivity
                Intent editIntent = new Intent(getApplicationContext(),ModifyActivity.class);

                editIntent.putExtra("wallpaper",new Gson().toJson(selected));

                //pass the wallpaper that needs to be modified to the intent

                //start ModifyActivity
                startActivity(editIntent);
            }
        });
    }
}
