package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;

public class ImageBitmapArgs {
    public Cursor cursor;
    public Context context;
    public int width;
    public int height;

    public ImageBitmapArgs(Cursor cursor, Context context, int width, int height) {
        this.cursor = cursor;
        this.context = context;
        this.width = width;
        this.height = height;
    }
}
