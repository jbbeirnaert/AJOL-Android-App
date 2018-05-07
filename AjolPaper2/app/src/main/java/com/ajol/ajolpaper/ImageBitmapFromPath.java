package com.ajol.ajolpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageBitmapFromPath implements Runnable {
    String path;
    int width = 40;
    int height = 40;
    Bitmap imageBitmap;

    public ImageBitmapFromPath(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        imageBitmap = BitmapFactory.decodeFile(path,bmpFactoryOptions);
    }
}
