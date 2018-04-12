package com.ajol.ajolpaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SettingsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String WALLPAPER_BUNDLE_NAME = "name";
    public static final String WALLPAPER_BUNDLE_X = "x";
    public static final String WALLPAPER_BUNDLE_Y = "y";
    public static final String WALLPAPER_BUNDLE_R = "r";
    public static final String WALLPAPER_BUNDLE_IMG = "img";

    private GoogleMap mMap;
    private Button myBGoToWallPaper;
    private Button myBGoToDefault;
    private EditText myEtRefreshTime;
    private Switch mySwitchDefault;
    private Button myButtonSave;
    private SharedPreferences preferences;

    public final String IS_GOING_TO_DEFAULT = "isGoingToDefault";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myBGoToWallPaper = findViewById(R.id.buttonGoToWallPaper);
        myBGoToDefault = findViewById(R.id.buttonGoToDefault);
        myEtRefreshTime = findViewById(R.id.etRefreshTime);
        mySwitchDefault = findViewById(R.id.switchDefault);
        myButtonSave = findViewById(R.id.buttonSave);

        preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        myEtRefreshTime.setText(String.valueOf(preferences.getInt("refreshTime", 30)));
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
                try{
                    refreshTime = Integer.parseInt(myEtRefreshTime.getText().toString());
                }catch (Exception e)
                {
                    refreshTime = 30;
                }
                editor.putInt("refreshTime", refreshTime);
                editor.putBoolean("useDefault", mySwitchDefault.isChecked());
                editor.apply();
                Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



}
