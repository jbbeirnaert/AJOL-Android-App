package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;

public class ImageBitmapArgs {
    public Cursor cursor;
    public int index;
    public Context context;

    public ImageBitmapArgs(Cursor cursor, int index, Context context) {
        this.cursor = cursor;
        this.index = index;
        this.context = context;
    }
}
