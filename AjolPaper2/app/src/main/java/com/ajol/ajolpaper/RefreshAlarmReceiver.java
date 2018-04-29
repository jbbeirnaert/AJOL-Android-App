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


                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    Toast.makeText(context,"Location: " + location.getLongitude() + "," + location.getLatitude(),Toast.LENGTH_SHORT).show();

                    Bitmap imageData = getWallpaper(context,location);

                    if (imageData != null) {
                        deviceWallpaperManager.setBitmap(imageData);
                    }
                    else {
                        Toast.makeText(context,"Ajol Paper failed to use the wallpapers!",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context,"Ajol Paper failed to read location!",Toast.LENGTH_SHORT).show();
                }
            }
            else if (useDefaults) {
                Bitmap imageData = getRandomDefault(context);

                if (imageData != null) {
                    deviceWallpaperManager.setBitmap(imageData);
                }
                else {
                    Toast.makeText(context,"Ajol Paper failed to use the defaults!",Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception exception) {
            //Owen: show failure
            Toast.makeText(context,"Ajol Paper failed to change the wallpaper!",Toast.LENGTH_SHORT).show();
        }
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

            Uri defaultUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_IMG)));

            try {
                return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(defaultUri));
            }
            catch (Exception e) {
                return null;
            }
        }
        else {
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

            Uri defaultUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_IMG)));

            try {
                return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(defaultUri));
            }
            catch (Exception e) {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
