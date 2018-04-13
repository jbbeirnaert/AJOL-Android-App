package com.ajol.ajolpaper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by owengallagher on 3/29/18.
 */

public class WallpaperListActivity extends AppCompatActivity {
    private Toolbar listToolbar;

    Cursor wallpapersCursor;
    SimpleCursorAdapter wallpapersCursorAdapter;
    ListView wallpapersView;

    private SQLiteDatabase db;
    private DatabaseLinker dbLinker; //Owen: this creates the database and links it to the activity

    public static final String[] wallpapersBind = {
            DatabaseConstants._id,
            DatabaseConstants.COLUMN_NAME,
            DatabaseConstants.COLUMN_X,
            DatabaseConstants.COLUMN_Y,
            DatabaseConstants.COLUMN_RADIUS,
            DatabaseConstants.COLUMN_IMG
    };

    public static final String[] wallpapersProjection = {
            DatabaseConstants.COLUMN_NAME,
            DatabaseConstants.COLUMN_RADIUS
            //DatabaseConstants.COLUMN_IMG      Owen: to be added when photos are supported
    };

    public static final int[] wallpapersMappings = {
            R.id.name,
            R.id.radius
            //R.id.photo_preview            Owen: to be added when photos are supported
    };

    @Override
    public void onCreate(Bundle savedInstanceState) { //Owen: this assumes list should display the wallpapers table
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //use toolbar in activity_list as the action bar
        listToolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(listToolbar);

        //connect to database
        dbLinker = new DatabaseLinker(getApplicationContext());
        db = dbLinker.getWritableDatabase();

        //handle whether the list should pull from defaults or from wallpapers
        Intent incomingIntent = getIntent();
        if (incomingIntent.getExtras().getBoolean(SettingsActivity.IS_GOING_TO_DEFAULT)) {
            //pull from defaults
            Toast.makeText(getApplicationContext(),"TO DO: pull from defaults table!",Toast.LENGTH_SHORT).show();
        }

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
                Double selectedX = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_X));
                Double selectedY = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_Y));
                Double selectedR = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_RADIUS));
                String selectedInfo = selectedName + " (" + selectedX + "," + selectedY + ")*" + selectedR;

                //create intent to start ModifyActivity
                Intent editIntent = new Intent(getApplicationContext(), ModifyActivity.class);

                //pass the wallpaper that needs to be modified to the intent
                Bundle wallpaperBundle = new Bundle();
                wallpaperBundle.putString(SettingsActivity.WALLPAPER_BUNDLE_NAME,selectedName);
                wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_X,selectedX);
                wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_Y,selectedY);
                wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_R,selectedR);

                editIntent.putExtras(wallpaperBundle);

                //start ModifyActivity
                startActivity(editIntent);
            }
        });

        populateWallpapers(5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.appbar_search);
        SearchView searchView = (SearchView) searchItem.getActionView(); //Owen: see this to set searchable configuration: https://developer.android.com/training/search/setup.html#create-sc

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle toolbar item clicks
        int id = item.getItemId();

        if (id == R.id.appbar_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(),SettingsActivity.class);

            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    //Owen: for testing add 5 example wallpapers
    //Owen: NOTE that this does not satisfy the assumption that all wallpaper names are unique
    public void populateWallpapers(int n) {
        if (wallpapersCursor.getCount() < n) {
            for (int i = 0; i < n; i++) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.COLUMN_NAME, String.valueOf(i));
                values.put(DatabaseConstants.COLUMN_X, (double) i * 100);
                values.put(DatabaseConstants.COLUMN_Y, (double) i * 100 + 100);
                values.put(DatabaseConstants.COLUMN_RADIUS, (double) i * 10 + 10);
                values.put(DatabaseConstants.COLUMN_IMG, (long) i * 1000);

                db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);
            }
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
}