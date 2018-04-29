package com.ajol.ajolpaper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static android.webkit.WebView.HitTestResult.IMAGE_TYPE;
import static com.ajol.ajolpaper.SettingsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by owengallagher on 3/29/18.
 * Updated by Linh Dang on 4/12/18.
 */


public class ModifyActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private static int LOAD_IMAGE_RESULTS = 1;
    private Button choose_button;
    Button save;
    private ImageView image;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        //pull wallpaper/default information and modify/add from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        String selectedName = selected.get
//        bundle.getString(WallpaperListActivity.wallpapersBind)




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        choose_button = (Button)findViewById(R.id.choose_button);
        choose_button.setOnClickListener(this);
        image = (ImageView)findViewById(R.id.image);
        save = (Button)findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //when user clicks on save create instance
                DatabaseLinker myDbLinker = new DatabaseLinker(getApplicationContext());
                SQLiteDatabase db = myDbLinker.getWritableDatabase();

                Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri imageUri = data.getData(); //Owen: make sure to save this uri to the database in the wallpapers/defaults table
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.photo_preview);
                imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                String imageString = imageUri.toString();

                ContentValues newImageValues = new ContentValues();

                //put image into newImageValues
                newImageValues.put(DatabaseConstants.COLUMN_IMG, imageString.toString());

                DatabaseLinker myDbLinker = new DatabaseLinker(getApplicationContext());
                SQLiteDatabase db = myDbLinker.getWritableDatabase();

                //insert image into db
                String whereClause = DatabaseConstants.COLUMN_NAME + " = " ;
                db.update(DatabaseConstants.TABLE_WALLPAPERS,newImageValues,"",null);
//                db.insert(DatabaseConstants.COLUMN_NAME,)
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No image found!", Toast.LENGTH_SHORT).show();
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Image stream still open!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Image selection cancelled!",Toast.LENGTH_SHORT).show();
        }

    }
    
    @Override
    public void onClick(View v) {
        // Create the Intent for Image Gallery.
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, LOAD_IMAGE_RESULTS);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        DatabaseLinker dbHelper = new DatabaseLinker(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] wallpapersBind = {
                DatabaseConstants._id,
                DatabaseConstants.COLUMN_NAME,
                DatabaseConstants.COLUMN_X,
                DatabaseConstants.COLUMN_Y,
                DatabaseConstants.COLUMN_RADIUS,
                DatabaseConstants.COLUMN_IMG
        };


        Cursor cursor = db.query(DatabaseConstants.TABLE_WALLPAPERS, //table to query
                wallpapersBind,
                null, //columns for where, Null will return all rows
                null, //values for where
                null, //Group By, null is no group by
                null, //Having, null says return all rows
                null
        );
        ArrayList<Wallpaper> wallpapers = new ArrayList<Wallpaper>();
        while(cursor.moveToNext()) {
            Wallpaper wallpaper = new Wallpaper();
            wallpaper.name = cursor.getString(
                    cursor.getColumnIndex(DatabaseConstants.COLUMN_NAME));
            wallpaper.x = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_X));
            wallpaper.y = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_Y));
            wallpaper.radius = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DatabaseConstants.COLUMN_RADIUS));
            wallpapers.add(wallpaper);
        }
        cursor.close();


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.501288, -78.018258))
                .anchor(0.5f, 0.5f)
                .title("BAC")
                .snippet("Radius: 15m")
        );
//
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(40.500404, -78.014396))
//                .anchor(0.5f, 0.5f)
//                .title("Ellis Hall")
//                .snippet("Radius: 30m")
//        );

    }

}
