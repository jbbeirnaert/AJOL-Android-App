package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WallpapersCursorAdapter extends SimpleCursorAdapter {
    private boolean getDefaults = true;

    public WallpapersCursorAdapter(Context context, int rowLayout, Cursor cursor, String[] projection, int[] mappings) { //Owen: the tutorial had objects as a List type, but that didn't work with super()...
        super(context, rowLayout, cursor, projection, mappings);

        //check whether the cursor is connected to the wallpapers table or to the defaults table
        for (int i=0; i<projection.length && getDefaults; i++) {
            if (projection[i].equals(DatabaseConstants.COLUMN_RADIUS)) {
                getDefaults = false;
            }
        }
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

        if (radiusView != null) {
            if (getDefaults) {
                ((ViewGroup) radiusView.getParent()).removeView(radiusView); //if the list is defaults, remove the radius field
            }
            else {
                String output = radiusView.getText() + " m";
                radiusView.setText(output);
            }
        }

        //connect db.delete(thisWallpaper) method to delete button
        Button deleteButton = view.findViewById(R.id.delete_button);

        final String name = (String) ((TextView) view.findViewById(R.id.name)).getText();
        final WallpapersCursorAdapter self = this;

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseLinker dbLinker = new DatabaseLinker(context);
                SQLiteDatabase db = dbLinker.getWritableDatabase();
                Cursor newCursor;

                //delete the wallpaper/default from the table!
                if (getDefaults) {
                    db.delete(DatabaseConstants.TABLE_DEFAULTS,DatabaseConstants.COLUMN_NAME + "='" + name + "'",null);

                    //update defaults list view
                    newCursor = db.query(DatabaseConstants.TABLE_DEFAULTS,WallpaperListActivity.defaultsBind,null,null,null,null,null);
                }
                else {
                    db.delete(DatabaseConstants.TABLE_WALLPAPERS, DatabaseConstants.COLUMN_NAME + "='" + name + "'", null);

                    //update wallpapers list view
                    newCursor = db.query(DatabaseConstants.TABLE_WALLPAPERS,WallpaperListActivity.wallpapersBind,null,null,null,null,null);
                }

                self.changeCursor(newCursor);
                self.notifyDataSetChanged();
            }
        });

        //show image preview
        TextView uriHolder = view.findViewById(R.id.uri_holder);
        Uri imageUri = Uri.parse(uriHolder.getText().toString());
        InputStream imageStream = null;
        ImageView photoPreview = view.findViewById(R.id.photo_preview_list);

        try {
            imageStream = context.getContentResolver().openInputStream(imageUri);
            photoPreview.setImageBitmap(BitmapFactory.decodeStream(imageStream));
        }
        catch (Exception e) {
            photoPreview.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_settings_black_24dp));
//            Toast.makeText(context,"Unable to load photo for " + name,Toast.LENGTH_SHORT).show();
        }
        finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                }
                catch (IOException e) {
//                    Toast.makeText(context,"Image stream still open!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

//Owen: see this for how to improve the above using convertView: http://www.worldbestlearningcenter.com/tips/Android-ListView-item-and-button-clickable.htm
