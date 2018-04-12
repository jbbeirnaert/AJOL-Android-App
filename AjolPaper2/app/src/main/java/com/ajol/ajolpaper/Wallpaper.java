package com.ajol.ajolpaper;

import android.graphics.ImageFormat;

import java.util.Vector;

/**
 * Created by owengallagher on 3/29/18.
 */

public class Wallpaper {
    public String imageUri;
    public String name;
    public double x;
    public double y;
    public double radius;

    public Wallpaper(String name) {
        this.imageUri = null;
        this.name = name;
        this.x = 0;
        this.y = 0;
        this.radius = 0;
    }
}
