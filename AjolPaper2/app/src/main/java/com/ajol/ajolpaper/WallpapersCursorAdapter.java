package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
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

        //connect ListActivity.deleteWallpaper() method to delete button
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output = "I want to delete a wallpaper!";
                Toast.makeText(context,output,Toast.LENGTH_SHORT).show();
            }
        });
    }
}

//Owen: see this for how to improve the above using convertView: http://www.worldbestlearningcenter.com/tips/Android-ListView-item-and-button-clickable.htm
