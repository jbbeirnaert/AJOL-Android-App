package com.ajol.ajolpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RefreshAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("preferences",Context.MODE_PRIVATE);
        boolean useDefaults = preferences.getBoolean("useDefault",true);

        try {
            //Owen: check location and set wallpaper
            WallpaperManager deviceWallpaperManager = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                //Owen: this can return null if no apps have calculated the device's current location
                Location location = getLastKnownLocation(locationManager,context);

                if (location != null) {
                    Toast.makeText(context,"Location: " + location.getLongitude() + "," + location.getLatitude(),Toast.LENGTH_SHORT).show();

                    Bitmap imageData = getWallpaper(context,location);

                    if (imageData != null) {
                        deviceWallpaperManager.setBitmap(imageData);
                    }
                    else {
                        Toast.makeText(context,"Background: failed to use the wallpapers!",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context,"Background: device location is unknown!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (useDefaults) {
                Bitmap imageData = getRandomDefault(context);

                if (imageData != null) {
                    deviceWallpaperManager.setBitmap(imageData);
                }
                else {
                    Toast.makeText(context,"Background: failed to use the defaults!",Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception exception) {
            //Owen: show failure
            Toast.makeText(context,"Background failed to change the wallpaper! " + exception.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    //Owen: uses any available providers
    private Location getLastKnownLocation(LocationManager locationManager, Context context) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            try {
                Location l = locationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            catch (SecurityException exception) {
                Toast.makeText(context,"Background: location is unknown.",Toast.LENGTH_SHORT).show();
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Nullable
    private Bitmap getRandomDefault(Context context) {
        DatabaseLinker dbLinker = new DatabaseLinker(context);
        SQLiteDatabase db = dbLinker.getWritableDatabase();

        String[] projection = {
                DatabaseConstants.COLUMN_IMG
        };

        Cursor cursor = db.query(DatabaseConstants.TABLE_DEFAULTS,projection,null,null,null,null,null);
        Random randomizer = new Random();
        int index = randomizer.nextInt(cursor.getCount()-1);

        if (index > -1) {
            cursor.moveToPosition(index);

            Bitmap imageBitmap = (new ImageBitmapFromCursor()).doInBackground(new ImageBitmapArgs(cursor,index));
            return imageBitmap;
        }
        else {
            cursor.close();
            db.close();
            return null;
        }
    }

    @Nullable
    private Bitmap getWallpaper(Context context, Location location) {
        //Owen: returns random wallpaper for testing
        DatabaseLinker dbLinker = new DatabaseLinker(context);
        SQLiteDatabase db = dbLinker.getWritableDatabase();

        String[] projection = {
                DatabaseConstants.COLUMN_IMG
        };

        Cursor cursor = db.query(DatabaseConstants.TABLE_WALLPAPERS,projection,null,null,null,null,null);
        Random randomizer = new Random();
        int index = randomizer.nextInt(cursor.getCount()-1);

        if (index > -1) {
            cursor.moveToPosition(index);

            Bitmap imageBitmap = (new ImageBitmapFromCursor()).doInBackground(new ImageBitmapArgs(cursor,index));
            return imageBitmap;
        }
        else {
            cursor.close();
            db.close();
            return null;
        }
    }
}
