package com.ajol.ajolpaper;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String WALLPAPER_BUNDLE_NAME = "name";
    public static final String WALLPAPER_BUNDLE_X = "x";
    public static final String WALLPAPER_BUNDLE_Y = "y";
    public static final String WALLPAPER_BUNDLE_R = "r";
    public static final String WALLPAPER_BUNDLE_IMG = "img";
    public static final String WALLPAPER_BUNDLE_IS_NEW = "new";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    public static final int MY_PERMISSIONS_REQUEST_GALLERY = 110;

    public static final int GET_REFRESH_BROADCAST = 0;
    public static final int LOAD_IMAGE_RESULTS = 1;

    private GoogleMap mMap;
    private Button myBGoToWallPaper;
    private Button myBGoToDefault;
    private EditText myEtRefreshTime;
    private Switch mySwitchDefault;
    private Button myButtonSave;
    private SharedPreferences preferences;

    public static final String IS_GOING_TO_DEFAULT = "isGoingToDefault";

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myBGoToWallPaper = findViewById(R.id.buttonGoToWallPaper);
        myBGoToDefault = findViewById(R.id.buttonGoToDefault);
        myEtRefreshTime = findViewById(R.id.etRefreshTime);
        mySwitchDefault = findViewById(R.id.switchDefault);
        myButtonSave = findViewById(R.id.buttonSave);

        preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        int refreshTime = preferences.getInt("refreshTime", 30);
        setupBackgroundProcess(refreshTime);

        myEtRefreshTime.setText(String.valueOf(refreshTime));
        mySwitchDefault.setChecked(preferences.getBoolean("useDefault", true));

        myBGoToWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WallpaperListActivity.class);
                intent.putExtra(IS_GOING_TO_DEFAULT, false);
                startActivity(intent);
            }
        });

        myBGoToDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WallpaperListActivity.class);
                intent.putExtra(IS_GOING_TO_DEFAULT, true);
                startActivity(intent);
            }
        });

        myButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = preferences.edit();
                int refreshTime;
                try {
                    refreshTime = Integer.parseInt(myEtRefreshTime.getText().toString());
                } catch (Exception e) {
                    refreshTime = 30;
                }
                editor.putInt("refreshTime", refreshTime);
                editor.putBoolean("useDefault", mySwitchDefault.isChecked());
                editor.apply();

                setupBackgroundProcess(refreshTime);

                Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        checkLocationPermission();
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

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
        for(int i=0; i<wallpapers.size(); i++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(wallpapers.get(i).y, wallpapers.get(i).x))
                    .anchor(0.5f, 0.5f)
                    .title(wallpapers.get(i).name)
                    .snippet("Radius: " + wallpapers.get(i).radius + "m")
            );
        }

//
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(40.501288, -78.018258))
//                .anchor(0.5f, 0.5f)
//                .title("BAC")
//                .snippet("Radius: 15m")
//        );
//
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(40.500404, -78.014396))
//                .anchor(0.5f, 0.5f)
//                .title("Ellis Hall")
//                .snippet("Radius: 30m")
//        );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f)); // should be the current location
                                // Logic to handle location object
                            }
                        }
                    });
        }
    }

    private void setupBackgroundProcess(int delayMinutes) {
        //Owen: set background service for Ajol Paper to refresh the wallpaper when the app UI is closed
        AlarmManager refreshTimer = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent refreshWallpaperIntent = PendingIntent.getBroadcast(getApplicationContext(),GET_REFRESH_BROADCAST,new Intent(getApplicationContext(),RefreshAlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
        refreshTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),delayMinutes*1000*60,refreshWallpaperIntent);
    }
}


//40.500141, -78.016441 juniata