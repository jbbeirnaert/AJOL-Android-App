package com.ajol.ajolpaper;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

public class ImageBlobArgs {
    public Uri uri;
    public Context context;
    public ImageView imageView;

    public ImageBlobArgs(Uri uri, Context context, ImageView imageView) {
        this.uri = uri;
        this.context = context;
        this.imageView = imageView;
    }
}
