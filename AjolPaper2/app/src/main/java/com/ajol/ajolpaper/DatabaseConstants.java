package com.ajol.ajolpaper;

import android.provider.BaseColumns;

public class DatabaseConstants implements BaseColumns {
    public static final String DB_NAME = "AjolWallpapers.db";
    public static final int DB_VERSION = 6;
    public static final String _id = "_id";
    public static final String TABLE_WALLPAPERS = "wallpapers";
    public static final String TABLE_DEFAULTS = "defaults";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMG = "img";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_RADIUS = "radius";
}
