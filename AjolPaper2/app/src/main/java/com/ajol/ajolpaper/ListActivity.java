package com.ajol.ajolpaper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owengallagher on 3/29/18.
 */

public class ListActivity extends AppCompatActivity {
    Cursor wallpapersCursor;
    SimpleCursorAdapter wallpapersCursorAdapter;
    ListView wallpapersView;

    private SQLiteDatabase db;
    private DatabaseLinker dbLinker; //Owen: this creates the database and links it to the activity

    @Override
    public void onCreate(Bundle savedInstanceState) { //Owen: this assumes list should display the wallpapers table
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //connect to database
        dbLinker = new DatabaseLinker(getApplicationContext());
        db = dbLinker.getWritableDatabase();

        //read wallpapers in from database to fill wallpapers[]
        String[] wallpapersBind = {
                DatabaseConstants._id,
                DatabaseConstants.COLUMN_NAME,
                DatabaseConstants.COLUMN_X,
                DatabaseConstants.COLUMN_Y,
                DatabaseConstants.COLUMN_RADIUS,
                DatabaseConstants.COLUMN_IMG
        };

        String[] wallpapersProjection = {
                DatabaseConstants.COLUMN_NAME,
                DatabaseConstants.COLUMN_RADIUS,
                DatabaseConstants.COLUMN_IMG
        };

        int[] wallpapersMappings = {
                R.id.name,
                R.id.radius,
                R.id.photo_preview
        };

        wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, null, null, null, null, null, null);

        //link wallpapersView to wallpapers
        wallpapersCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.list_item,wallpapersCursor,wallpapersProjection,wallpapersMappings,0);
        wallpapersView = findViewById(R.id.wallpapers_view);
        wallpapersView.setAdapter(wallpapersCursorAdapter);

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

    //Owen: for testing
    public void populateWallpapers(int n) {
        for (int i=0; i<n; i++) {
            //add 5 example wallpapers
        }
    }

    //Owen: this method fires when the delete button in the wallpapersView is clicked. CURRENTLY BROKEN
    public void deleteWallpaper(View view) {
        String output = "I want to delete a wallpaper!";
        Toast toast = Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT);
        toast.show();
    }
}
