package com.ajol.ajolpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

public class ImageBitmapFromBlob extends AsyncTask<byte[],Integer,Bitmap> {
    @Override
    public Bitmap doInBackground(byte[]... params) {
        Bitmap bitmap = getImageBitmap(params[0]);

        return bitmap;
    }

    @Nullable
    private Bitmap getImageBitmap(byte[] imageBlob) {
        return BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
    }
}
