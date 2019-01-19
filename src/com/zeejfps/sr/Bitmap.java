package com.zeejfps.sr;

import java.util.Arrays;

public class Bitmap {

    public final int width;
    public final int height;
    public final int[] pixels;

    private Bitmap(int[] pixels, int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public static Bitmap create(int width, int height) {
        return new Bitmap(new int[width * height], width, height);
    }

    public static Bitmap ofColor(int width, int height, int color) {
        int[] pixels = new int[width*height];
        Arrays.fill(pixels, color);
        return new Bitmap(pixels, width, height);
    }

    public static Bitmap of(int[] pixels, int width, int height) {
        return new Bitmap(pixels, width, height);
    }

    public static Bitmap copyOf(int[] pixels, int width, int height) {
        return new Bitmap(Arrays.copyOf(pixels, pixels.length), width, height);
    }

    public static Bitmap copyOf(Bitmap bitmap) {
        return copyOf(bitmap.pixels, bitmap.width, bitmap.height);
    }

}
