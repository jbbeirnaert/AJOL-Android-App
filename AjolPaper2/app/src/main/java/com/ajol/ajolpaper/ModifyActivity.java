package com.ajol.ajolpaper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by owengallagher on 3/29/18.
 * Updated by Linh Dang on 4/12/18.
 */


public class ModifyActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private Button choose_button;
    Button save;
    private ImageView image;
    private String imagePath = "";
//    private byte[] imageBlob; //Owen: for storing a copy of the image
    private FusedLocationProviderClient mFusedLocationClient;
    private Location deviceLocation;

    private boolean isDefault = false;
    private boolean isNew = false;

    private int id;
    private String name = "";
    private Double xLocation;
    private Double yLocation;
    private Double radius;
    private MarkerOptions markerOptions = new MarkerOptions();
    private Marker marker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        //pull wallpaper/default information and modify/add from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isDefault = bundle.getBoolean(SettingsActivity.IS_GOING_TO_DEFAULT);
        isNew = bundle.getBoolean(SettingsActivity.WALLPAPER_BUNDLE_IS_NEW);
        id = bundle.getInt(SettingsActivity.WALLPAPER_BUNDLE_ID);

        if (isDefault) {
            //Owen: remove map view
            FrameLayout mapView = findViewById(R.id.include);
            ((ViewGroup) mapView.getParent()).removeView(mapView);

            //Owen: fix views constrained to deleted mapView
            EditText nameView = findViewById(R.id.name);
            ConstraintLayout.LayoutParams nameViewParams = (ConstraintLayout.LayoutParams) nameView.getLayoutParams();
            nameViewParams.topToBottom = ConstraintLayout.LayoutParams.UNSET;
            nameViewParams.topToTop = R.id.parent;

            if (!isNew) {
                name = bundle.getString(SettingsActivity.WALLPAPER_BUNDLE_NAME);
                nameView.setText(name);
            }
        }
        else {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if (isNew) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //Owen: this can grab the device location
                getDeviceLocation();
            }
            else {
                name = bundle.getString(SettingsActivity.WALLPAPER_BUNDLE_NAME);
                radius = bundle.getDouble(SettingsActivity.WALLPAPER_BUNDLE_R);
                xLocation = bundle.getDouble(SettingsActivity.WALLPAPER_BUNDLE_X);
                yLocation = bundle.getDouble(SettingsActivity.WALLPAPER_BUNDLE_Y);

                ((EditText) findViewById(R.id.name)).setText(name);
                ((EditText) findViewById(R.id.radius)).setText(radius.toString());

            }
        }

        choose_button = findViewById(R.id.choose_button);
        choose_button.setOnClickListener(this);
        image = findViewById(R.id.image);

        DatabaseLinker dbLinker = new DatabaseLinker(this);
        SQLiteDatabase db = dbLinker.getReadableDatabase();
        String selection = DatabaseConstants._id + " = " + id;
        Cursor imageCursor;
        if (isDefault) {
            imageCursor = db.query(DatabaseConstants.TABLE_DEFAULTS,new String[] {DatabaseConstants.COLUMN_IMG},selection,null,null,null,null);
        }
        else {
            imageCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS,new String[] {DatabaseConstants.COLUMN_IMG},selection,null,null,null,null);
        }
        if (!isNew) {
            imageCursor.moveToFirst();
            imagePath = imageCursor.getString(imageCursor.getColumnIndex(DatabaseConstants.COLUMN_IMG));
        }
        Bitmap imageBitmap = (new ImageBitmapFromCursor()).doInBackground(new ImageBitmapArgs(imageCursor,0,this));
        if (imageBitmap != null) {
            ImageView photoPreview = findViewById(R.id.photo_preview);
            photoPreview.setImageBitmap(imageBitmap);
        }
        db.close();
        dbLinker.close();

        save = findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //when user clicks on save create instance
                DatabaseLinker myDbLinker = new DatabaseLinker(getApplicationContext());
                SQLiteDatabase db = myDbLinker.getWritableDatabase();

                ContentValues newEntry = new ContentValues();

                EditText nameView = findViewById(R.id.name);
                name = nameView.getText().toString();

                if (!name.equals("") && !imagePath.equals("")) {
                    newEntry.put(DatabaseConstants.COLUMN_NAME, name);
                    newEntry.put(DatabaseConstants.COLUMN_IMG, imagePath);
                    String targetTable = DatabaseConstants.TABLE_WALLPAPERS;

                    //insert image into db
                    if (isDefault) {
                        targetTable = DatabaseConstants.TABLE_DEFAULTS;
                    }
                    else {
                        EditText radiusView = findViewById(R.id.radius);
                        radius = Double.parseDouble(radiusView.getText().toString());

                        newEntry.put(DatabaseConstants.COLUMN_X,xLocation);
                        newEntry.put(DatabaseConstants.COLUMN_Y,yLocation);
                        newEntry.put(DatabaseConstants.COLUMN_RADIUS,radius);
                    }

                    if (isNew) {
                        db.insert(targetTable,null, newEntry);
                    }
                    else {
                        String whereClause = DatabaseConstants._id + " = " + id;
                        db.update(targetTable, newEntry, whereClause, null);
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), "Saved: " + newEntry.toString(), Toast.LENGTH_LONG);
                    toast.show();
                }

                db.close();
                dbLinker.close();
            }
        });
    }

    //Owen: adapted from SettingsActivity.checkLocationPermission
    public void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            deviceLocation = location;
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri imageUri = data.getData();
            imagePath = getPathFromUri(imageUri);
            ImageView imageView = findViewById(R.id.photo_preview);

            if (gotStorageReadPermission()) {
                Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();

                ImageBitmapFromPath loadBitmap = new ImageBitmapFromPath(imagePath);
                (new Thread(loadBitmap)).start();

                while (loadBitmap.imageBitmap == null) {
                    imageView.setImageBitmap(loadBitmap.imageBitmap);
                }
                imageView.setImageBitmap(loadBitmap.imageBitmap);
            }
            else {
                Toast.makeText(getApplicationContext(),"Storage access denied.",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Image selection cancelled!",Toast.LENGTH_SHORT).show();
        }
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = null;
        String output = "";
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = this.getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            output = cursor.getString(column_index);
        }
        catch (Exception e) {
            Toast.makeText(this,"Couldn't get image file path.",Toast.LENGTH_SHORT).show();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return output;
    }

    private boolean gotStorageReadPermission() {
        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
                return true;
            } catch (Exception e) {
                Toast.makeText(this, "Couldn't get permission to read external storage.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            return true;
        }
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
    
    @Override
    public void onClick(View v) {
        // Create the Intent for Image Gallery.
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, SettingsActivity.LOAD_IMAGE_RESULTS);
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

        //add wallpaper location to map
        if (xLocation != null && yLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yLocation, xLocation), 15.0f));

            markerOptions.position(new LatLng(yLocation, xLocation));
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.title(name);
            markerOptions.snippet("Radius: " + radius + "m");

            marker = mMap.addMarker(markerOptions);
        }
        else if (deviceLocation != null) { //Owen: move map to current location if defined
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()), 15.0f)); // should be the current location
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                xLocation = latLng.longitude;
                yLocation = latLng.latitude;

                if (marker == null) {
                    markerOptions.position(latLng);
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.title(name);
                    markerOptions.snippet("Radius: " + radius + "m");

                    marker = mMap.addMarker(markerOptions);
                }
                else {
                    marker.setPosition(latLng);
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setTitle(name);
                    marker.setSnippet("Radius: " + radius + "m");
                }
            }
        });
    }


}
