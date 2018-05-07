package com.ajol.ajolpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageBitmapFromPath implements Runnable {
    String path;
    Bitmap imageBitmap;

    public ImageBitmapFromPath(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        imageBitmap = BitmapFactory.decodeFile(path);
    }
}
