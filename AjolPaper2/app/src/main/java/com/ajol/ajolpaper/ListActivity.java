package com.ajol.ajolpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owengallagher on 3/29/18.
 */

public class ListActivity extends AppCompatActivity {
    List<Wallpaper> wallpapers = new ArrayList<>(0);
    WallpaperListAdapter wallpapersListAdapter;
    ListView wallpapersView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //read wallpapers in from database to fill wallpapers[]

        //link wallpapersView to wallpapers
        wallpapersListAdapter = new WallpaperListAdapter(getApplicationContext(),R.layout.list_item,wallpapers);
        wallpapersListAdapter.setNotifyOnChange(true); //when wallpapers[] changes, wallpapersView should change too
        wallpapersView = findViewById(R.id.wallpapers_view);
        wallpapersView.setAdapter(wallpapersListAdapter);

        //Owen: make the list items clickable (you don't have to press on its edit button)
        wallpapersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                //this should store the wallpaper from wallpapers[] in selected
                Wallpaper selected = (Wallpaper) listView.getItemAtPosition(position);

                //create intent to start ModifyActivity
//                Intent editIntent = new Intent(getApplicationContext(), ModifyActivity.class);

                //pass the wallpaper that needs to be modified to the intent
//                editIntent.putExtra("wallpaper", new Gson().toJson(selected));

                //start ModifyActivity
//                startActivity(editIntent);

                Toast toast = Toast.makeText(getApplicationContext(),selected.name,Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        populateWallpapers(5);
    }

    public void populateWallpapers(int n) {
        for (int i=0; i<n; i++) {
            wallpapers.add(new Wallpaper(String.valueOf(i)));
        }
    }

    //Owen: this method fires when the delete button in the wallpapersView is clicked. CURRENTLY BROKEN
    public void deleteWallpaper(View view) {
        String output = "I want to delete a wallpaper!";
        Toast toast = Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT);
        toast.show();
    }
}
