package com.ajol.ajolpaper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

public class ImageBitmapFromCursor extends AsyncTask<ImageBitmapArgs,Integer,Bitmap> {
    @Override
    public Bitmap doInBackground(ImageBitmapArgs... params) {
        Bitmap bitmap = getImageBitmap(params[0].cursor,params[0].index,params[0].context);

        return bitmap;
    }

    @Nullable
    private Bitmap getImageBitmap(Cursor imageCursor, int index, Context context) {
        if (imageCursor.moveToPosition(index)) {
            imageCursor.moveToFirst();

//            Toast.makeText(context,"Getting bitmap...",Toast.LENGTH_SHORT).show();
            String imagePath = imageCursor.getString(imageCursor.getColumnIndex(DatabaseConstants.COLUMN_IMG));

            if (!imageCursor.isClosed()) {
                imageCursor.close();
            }

            Bitmap output = BitmapFactory.decodeFile(imagePath);

            return output;
        }

        return null;
    }
}
