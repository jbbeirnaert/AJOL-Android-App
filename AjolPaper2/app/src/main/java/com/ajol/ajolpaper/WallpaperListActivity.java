package com.ajol.ajolpaper;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by owengallagher on 3/29/18.
 */

public class WallpaperListActivity extends AppCompatActivity {
    private Toolbar listToolbar;
    private SearchView searchView = null;

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
            DatabaseConstants._id,
            DatabaseConstants.COLUMN_NAME,
            DatabaseConstants.COLUMN_RADIUS
    };

    public static final int[] wallpapersMappings = {
            R.id.id_holder,
            R.id.name,
            R.id.radius
    };

    public static final String[] defaultsBind = {
            DatabaseConstants._id,
            DatabaseConstants.COLUMN_NAME,
            DatabaseConstants.COLUMN_IMG
    };

    public static final String[] defaultsProjection = {
            DatabaseConstants._id,
            DatabaseConstants.COLUMN_NAME,
            DatabaseConstants.COLUMN_IMG
    };

    public static final int[] defaultsMappings = {
            R.id.id_holder,
            R.id.name
    };

    private boolean wasDefaults = false;
    private boolean getDefaults = false;
    private ArrayList<String> searchTerms = new ArrayList<>(0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //use toolbar in activity_list as the action bar
        listToolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(listToolbar);

        //connect to database
        dbLinker = new DatabaseLinker(getApplicationContext());
        db = dbLinker.getWritableDatabase();

        //handle whether the list should pull from defaults or from wallpapers
        Bundle incomingIntentBundle = getIntent().getExtras();
        try {
            getDefaults = incomingIntentBundle.getBoolean(SettingsActivity.IS_GOING_TO_DEFAULT);
        }
        catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),"DB table not specified!",Toast.LENGTH_SHORT).show();
            getDefaults = wasDefaults;
        }

        if (getDefaults) {
            //read defaults in from database
            wallpapersCursor = db.query(DatabaseConstants.TABLE_DEFAULTS,defaultsBind,null,null,null,null,null,null);

            //link wallpapersView to wallpapers (default wallpapers)
            wallpapersCursorAdapter = new WallpapersCursorAdapter(getApplicationContext(),R.layout.list_item,wallpapersCursor,defaultsProjection,defaultsMappings);
        }
        else {
            //read wallpapers in from database
            wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, null, null, null, null, null, null);

            //link wallpapersView to wallpapers
            wallpapersCursorAdapter = new WallpapersCursorAdapter(getApplicationContext(),R.layout.list_item,wallpapersCursor,wallpapersProjection,wallpapersMappings);
        }
        wasDefaults = getDefaults;

        wallpapersView = findViewById(R.id.wallpapers_view);
        wallpapersView.setAdapter(wallpapersCursorAdapter);

        //Owen: make the list items clickable
        wallpapersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                //this should store the wallpaper from wallpapers[] in selected
                Cursor selected = (Cursor) listView.getItemAtPosition(position);
                int selectedId = Integer.parseInt(selected.getString(selected.getColumnIndex(DatabaseConstants._id)));
                String selectedName = selected.getString(selected.getColumnIndex(DatabaseConstants.COLUMN_NAME));

                //create intent to start ModifyActivity
                Intent editIntent = new Intent(getApplicationContext(), ModifyActivity.class);

                //pass the wallpaper that needs to be modified to the intent
                Bundle wallpaperBundle = new Bundle();
                wallpaperBundle.putBoolean(SettingsActivity.IS_GOING_TO_DEFAULT,getDefaults);
                wallpaperBundle.putInt(SettingsActivity.WALLPAPER_BUNDLE_ID,selectedId);
                wallpaperBundle.putBoolean(SettingsActivity.WALLPAPER_BUNDLE_IS_NEW,false);
                wallpaperBundle.putString(SettingsActivity.WALLPAPER_BUNDLE_NAME,selectedName);

                if (!getDefaults) {
                    Double selectedX = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_X));
                    Double selectedY = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_Y));
                    Double selectedR = selected.getDouble(selected.getColumnIndex(DatabaseConstants.COLUMN_RADIUS));

                    wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_X,selectedX);
                    wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_Y,selectedY);
                    wallpaperBundle.putDouble(SettingsActivity.WALLPAPER_BUNDLE_R,selectedR);
                }

                editIntent.putExtras(wallpaperBundle);

                //start ModifyActivity
                startActivity(editIntent);
            }
        });

        handleIntent(getIntent());

//        if (searchTerms.size() == 0) {
//            if (getDefaults) {
//                populateDefaults(5);
//            } else {
//                populateWallpapers(5);
//            }
//        }

        if (!checkPermissionForReadExternalStorage()) {
            requestPermissionForReadExternalStorage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.appbar_search);
        searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        try {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }
        catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),"Search bar is broken...",Toast.LENGTH_SHORT).show();
        }

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
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbLinker.close();
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (searchView != null) {
            //close search view
            if (!searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            }
        }

        //handle search query (when search is performed, this activity is started again with the search query in the intent)
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);

            searchTerms = parseQuery(searchQuery); //split by spaces and remove non-alphanumeric characters

            //create where clause for to pull only desired wallpapers/defaults from the database
            StringBuilder whereClause = new StringBuilder();
            if (searchTerms.size() != 0) {
                for (int i=0; i<searchTerms.size(); i++) {
                    whereClause.append("(");
                    whereClause.append(DatabaseConstants.COLUMN_NAME);
                    whereClause.append(" LIKE '%");
                    whereClause.append(searchTerms.get(i));
                    whereClause.append("%')");

                    if (i < searchTerms.size()-1) {
                        whereClause.append(" OR ");
                    }
                }

                if (getDefaults) {
                    wallpapersCursor = db.query(DatabaseConstants.TABLE_DEFAULTS, defaultsBind, whereClause.toString(), null, null, null, null, null);
                }
                else {
                    wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, whereClause.toString(), null, null, null, null, null);
                }

                wallpapersCursorAdapter.changeCursor(wallpapersCursor);
                wallpapersCursorAdapter.notifyDataSetChanged();
            }
        }
    }

    //Owen: for testing add 5 example wallpapers
    //Owen: NOTE that this does not satisfy the assumption that all wallpaper names are unique
    public void populateWallpapers(int n) {
        if (wallpapersCursor.getCount() < n) {

            ContentValues values = new ContentValues();
            values.put(DatabaseConstants.COLUMN_NAME, "BAC");
            values.put(DatabaseConstants.COLUMN_Y, 40.501288);
            values.put(DatabaseConstants.COLUMN_X, -78.018258);
            values.put(DatabaseConstants.COLUMN_RADIUS, (double) 15);
            values.put(DatabaseConstants.COLUMN_IMG, "URLImage");
            db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);

            values.clear();
            values.put(DatabaseConstants.COLUMN_NAME, "Ellis Hall");
            values.put(DatabaseConstants.COLUMN_Y, 40.500404);
            values.put(DatabaseConstants.COLUMN_X, -78.014396);
            values.put(DatabaseConstants.COLUMN_RADIUS, (double) 30);
            values.put(DatabaseConstants.COLUMN_IMG, "URLImage");
            db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);

            values.clear();
            values.put(DatabaseConstants.COLUMN_NAME, "TNT");
            values.put(DatabaseConstants.COLUMN_Y, 40.502137);
            values.put(DatabaseConstants.COLUMN_X, -78.017113);
            values.put(DatabaseConstants.COLUMN_RADIUS, (double) 40);
            values.put(DatabaseConstants.COLUMN_IMG, "URLImage");
            db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);

            values.clear();
            values.put(DatabaseConstants.COLUMN_NAME, "Good Hall");
            values.put(DatabaseConstants.COLUMN_Y, 40.499451);
            values.put(DatabaseConstants.COLUMN_X, -78.017825);
            values.put(DatabaseConstants.COLUMN_RADIUS, (double) 30);
            values.put(DatabaseConstants.COLUMN_IMG, "URLImage");
            db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);

            values.clear();
            values.put(DatabaseConstants.COLUMN_NAME, "Stone Church");
            values.put(DatabaseConstants.COLUMN_Y, 40.498488);
            values.put(DatabaseConstants.COLUMN_X, -78.016745);
            values.put(DatabaseConstants.COLUMN_RADIUS, (double) 20);
            values.put(DatabaseConstants.COLUMN_IMG, "URLImage");
            db.insert(DatabaseConstants.TABLE_WALLPAPERS, null, values);


            //update wallpapers list
            wallpapersCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, wallpapersBind, null, null, null, null, null, null);
            wallpapersCursorAdapter.changeCursor(wallpapersCursor);
            wallpapersCursorAdapter.notifyDataSetChanged();
        }
    }

    //Owen: pull search terms from the query string and use regex to remove unwanted characters
    public ArrayList<String> parseQuery(String terms) {
        ArrayList<String> output = new ArrayList<>(1);

        String[] input = terms.split(" ");
        Pattern validPattern = Pattern.compile("[A-Za-z0-9]");
        Matcher validator;
        String validInput;

        for (int i=0; i<input.length; i++) {
            validInput = "";
            validator = validPattern.matcher(input[i]);

            while (validator.find()) {
                validInput += validator.group();
            }

            output.add(validInput);
        }

        return output;
    }

    //Owen: for testing add 5 example defaults
    //Owen: NOTE that this does not satisfy the assumption that all default names are unique
    public void populateDefaults(int n) {
        if (wallpapersCursor.getCount() < n) {
            for (int i = 0; i < n; i++) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.COLUMN_NAME, generateName(6));
                values.put(DatabaseConstants.COLUMN_IMG, String.valueOf(i * 1000));

                db.insert(DatabaseConstants.TABLE_DEFAULTS, null, values);
            }

            //update defaults list
            wallpapersCursor = db.query(DatabaseConstants.TABLE_DEFAULTS, defaultsBind, null, null, null, null, null, null);
            wallpapersCursorAdapter.changeCursor(wallpapersCursor);
            wallpapersCursorAdapter.notifyDataSetChanged();
        }
    }

    //Owen: just for fun
    public String generateName(int len) {
        Random generator = new Random();
        boolean evens = false;
        char[] opens = {'a','e','i','o','u','y'};

        String output = "";

        if (generator.nextBoolean()) {
            evens = true;
        }

        for (int l=0; l<len; l++) {
            if ((evens && l % 2 == 0) || (!evens && l % 2 != 0)) {
                int nextLetter = 97 + generator.nextInt(25);

                output += (char) nextLetter;
            }
            else {
                output += opens[generator.nextInt(opens.length-1)];
            }
        }

        return output;
    }

    //Owen: this method fires when the add floating action button is clicked.
    public void addWallpaper(View view) {
        Intent addIntent = new Intent(getApplicationContext(),ModifyActivity.class);
        Bundle bundle = new Bundle();

        bundle.putBoolean(SettingsActivity.IS_GOING_TO_DEFAULT,getDefaults);
        bundle.putBoolean(SettingsActivity.WALLPAPER_BUNDLE_IS_NEW,true);

        addIntent.putExtras(bundle);
        startActivity(addIntent);
    }

    private boolean checkPermissionForReadExternalStorage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    private void requestPermissionForReadExternalStorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SettingsActivity.MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
    }
}
