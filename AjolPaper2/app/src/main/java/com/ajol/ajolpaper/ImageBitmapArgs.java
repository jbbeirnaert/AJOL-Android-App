package com.ajol.ajolpaper;

import android.database.Cursor;

public class ImageBitmapArgs {
    public Cursor cursor;
    public int index;

    public ImageBitmapArgs(Cursor cursor, int index) {
        this.cursor = cursor;
        this.index = index;
    }
}
