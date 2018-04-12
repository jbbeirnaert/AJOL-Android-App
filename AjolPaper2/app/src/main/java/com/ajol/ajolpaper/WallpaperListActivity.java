package com.ajol.ajolpaper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by owengallagher on 3/29/18.
 */

public class WallpaperListActivity extends AppCompatActivity {
    Cursor wallpapersCursor;
    SimpleCursorAdapter wallpapersCursorAdapter;
    ListView wallpapersView;

    private SQLiteDatabase db;
    private DatabaseLinker dbLinker; //Owen: this creates the database and links it to the activity

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
            DatabaseConstants.COLUMN_RADIUS
            //DatabaseConstants.COLUMN_IMG      Owen: to be added when photos are supported
    };

    int[] wallpapersMappings = {
            R.id.name,
            R.id.radius
            //R.id.photo_preview            Owen: to be added when photos are supported
    };

    @Override
    public void onCreate(Bundle savedInstanceState) { //Owen: this assumes list should display the wallpapers table
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //connect to database
        dbLinker = new DatabaseLinker(getApplicationContext());
        db = dbLinker.getWritableDatabase();

        //read wallpapers in from database to fill wallpapers[]
        wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, null, null, null, null, null, null);

        //link wallpapersView to wallpapers
        wallpapersCursorAdapter = new WallpapersCursorAdapter(getApplicationContext(),R.layout.list_item,wallpapersCursor,wallpapersProjection,wallpapersMappings);
        wallpapersView = findViewById(R.id.wallpapers_view);
        wallpapersView.setAdapter(wallpapersCursorAdapter);

        //Owen: make the list items clickable
        wallpapersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                //this should store the wallpaper from wallpapers[] in selected
                Cursor selected = (Cursor) listView.getItemAtPosition(position);
                String selectedName = selected.getString(selected.getColumnIndex(DatabaseConstants.COLUMN_NAME));

                //create intent to start ModifyActivity
//                Intent editIntent = new Intent(getApplicationContext(), ModifyActivity.class);

                //pass the wallpaper that needs to be modified to the intent
//                editIntent.putExtra("wallpaper", WALLPAPER_DATA);

                //start ModifyActivity
//                startActivity(editIntent);

                Toast toast = Toast.makeText(getApplicationContext(),"I want to modify " + selectedName,Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        populateWallpapers(5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    //Owen: for testing add 5 example wallpapers
    public void populateWallpapers(int n) {
        if (wallpapersCursor.getCount() < n) {
            Toast.makeText(getApplicationContext(),"Populating wallpapers table!",Toast.LENGTH_SHORT).show();

            for (int i = 0; i < n; i++) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.COLUMN_NAME, String.valueOf(i));
                values.put(DatabaseConstants.COLUMN_X, (double) i * 100);
                values.put(DatabaseConstants.COLUMN_Y, (double) i * 100 + 100);
                values.put(DatabaseConstants.COLUMN_RADIUS, (double) i * 10);
                values.put(DatabaseConstants.COLUMN_IMG, (long) i * 1000);

                db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Not populating wallpapers table!",Toast.LENGTH_SHORT).show();
        }

        //update wallpapers list
        wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, null, null, null, null, null, null);
        wallpapersCursorAdapter.changeCursor(wallpapersCursor);
        wallpapersCursorAdapter.notifyDataSetChanged();
    }

    //Owen: this method fires when the add floating action button is clicked.
    public void addWallpaper(View view) {
        //FILL IN
        String output = "I want to add a wallpaper!";
        Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT).show();
    }

    //Owen: this method fires when the delete button in the wallpapersView is clicked. CURRENTLY BROKEN
//    public void deleteWallpaper(View view) {
//        String output = "I want to delete a wallpaper!";
//        Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT).show();
//    }
}
