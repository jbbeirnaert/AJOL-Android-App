package com.ajol.ajolpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageBlob extends AsyncTask<ImageBlobArgs,Integer,byte[]> {
    @Override
    public byte[] doInBackground(ImageBlobArgs... params) {
        Uri imageUri = params[0].uri;
        Context caller = params[0].context;
        ImageView imageView = params[0].imageView;
        Bitmap imageBitmap = null;
        byte[] blob = null;

        try {
            InputStream imageStream = caller.getContentResolver().openInputStream(imageUri);
            imageBitmap = BitmapFactory.decodeStream(imageStream);
        }
        catch (Exception exception) {
            //do nothing
        }

        if (imageBitmap != null) {
            blob = getImageBlob(imageBitmap);
        }

        imageView.setImageBitmap(imageBitmap);

        return blob;
    }

    private byte[] getImageBlob(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        try {
            outputStream.close();
        }
        catch (Exception e) {
            //continue anyway
        }
        return outputStream.toByteArray();
    }
}
