package com.ajol.ajolpaper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseLinker extends SQLiteOpenHelper {
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String TEXT_TYPE = "text";
    private static final String ZONE_TYPE = "double";
    private static final String IMG_TYPE = "text"; //Owen: I think images will be URI strings.

    private static final String CREATE_TABLE = "create table";
    private static final String DROP_TABLE = "drop table if exists";

    private static final String CREATE_TABLE_WALLPAPERS = CREATE_TABLE + SPACE +
            DatabaseConstants.TABLE_WALLPAPERS + " (" +
            DatabaseConstants._id + " integer primary key" + COMMA +
            DatabaseConstants.COLUMN_NAME + SPACE + TEXT_TYPE + COMMA +
            DatabaseConstants.COLUMN_RADIUS + SPACE + ZONE_TYPE + COMMA + //Owen: I assume numbers will be long
            DatabaseConstants.COLUMN_X + SPACE + ZONE_TYPE + COMMA +
            DatabaseConstants.COLUMN_Y + SPACE + ZONE_TYPE + COMMA +
            DatabaseConstants.COLUMN_IMG + SPACE + IMG_TYPE + " )";

    private static final String CREATE_TABLE_DEFAULTS = CREATE_TABLE + SPACE +
            DatabaseConstants.TABLE_DEFAULTS + " (" +
            DatabaseConstants._id + " integer primary key" + COMMA +
            DatabaseConstants.COLUMN_NAME + SPACE + TEXT_TYPE + " )";

    private static final String DELETE_TABLE_WALLPAPERS = DROP_TABLE + SPACE + DatabaseConstants.TABLE_WALLPAPERS;
    private static final String DELETE_TABLE_DEFAULTS = DROP_TABLE + SPACE + DatabaseConstants.TABLE_DEFAULTS;

    public DatabaseLinker(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WALLPAPERS);
        db.execSQL(CREATE_TABLE_DEFAULTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //Owen: to be modified to not delete data in db
        db.execSQL(DELETE_TABLE_WALLPAPERS);
        db.execSQL(DELETE_TABLE_DEFAULTS);
        onCreate(db);
    }
}
