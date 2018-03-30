package com.ajol.ajolpaper;

import android.media.Image;

import java.util.Vector;

/**
 * Created by owengallagher on 3/29/18.
 */

public class Wallpaper {
    public Image image;
    public String name;
    public Vector<Double> location = new Vector<>(2);
    public double radius;

    public Wallpaper(String name) {
        this.image = null;
        this.name = name;
        this.location.set(0,0.0);
        this.location.set(1,0.0);
        this.radius = 0;
    }
}
