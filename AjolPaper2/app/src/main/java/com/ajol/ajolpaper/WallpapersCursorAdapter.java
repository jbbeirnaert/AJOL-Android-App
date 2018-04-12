package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WallpapersCursorAdapter extends SimpleCursorAdapter {

    public WallpapersCursorAdapter(Context context, int rowLayout, Cursor cursor, String[] projection, int[] mappings) { //Owen: the tutorial had objects as a List type, but that didn't work with super()...
        super(context, rowLayout, cursor, projection, mappings);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);

        //add units for distance
        TextView radiusView = view.findViewById(R.id.radius);
        radiusView.setText(radiusView.getText() + " m");

        //connect db.delete(thisWallpaper) method to delete button
        Button deleteButton = view.findViewById(R.id.delete_button);

        final String name = (String) ((TextView) view.findViewById(R.id.name)).getText();
        final WallpapersCursorAdapter self = this;

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseLinker dbLinker = new DatabaseLinker(context);
                SQLiteDatabase db = dbLinker.getWritableDatabase();

                //delete the wallpaper from the table!
                db.delete(DatabaseConstants.TABLE_WALLPAPERS,DatabaseConstants.COLUMN_NAME + "='" + name + "'",null);

                //update wallpapers list view
                Cursor newCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS,WallpaperListActivity.wallpapersBind,null,null,null,null,null);

                self.changeCursor(newCursor);
                self.notifyDataSetChanged();
            }
        });
    }
}

//Owen: see this for how to improve the above using convertView: http://www.worldbestlearningcenter.com/tips/Android-ListView-item-and-button-clickable.htm
