package com.ajol.ajolpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WallpaperListAdapter extends ArrayAdapter {
    private int resource;

    public WallpaperListAdapter(Context context, int resource, List<Wallpaper> objects) { //Owen: the tutorial had objects as a List type, but that didn't work with super()...
        super(context, resource, objects);

        this.resource = resource;
    }

    //Owen: customize adapter to pick out what data from each list item will get put into each view in the row item layout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) (getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View rowView = inflater.inflate(resource, parent, false);

        ImageView photoPreview = rowView.findViewById(R.id.photo_preview);
        TextView name = rowView.findViewById(R.id.name);
        //...we can add support for more wallpaper data display later if we want to show the radius

        //set photoPreview's image reference
        //set name's text
        try {
            Wallpaper wallpaper = (Wallpaper) getItem(position);
            name.setText(wallpaper.name);
        } catch (NullPointerException e) {
            name.setText(R.string.no_name);
        }

        return rowView;
    }
}

//Owen: see this for how to improve the above using convertView: http://www.worldbestlearningcenter.com/tips/Android-ListView-item-and-button-clickable.htm
