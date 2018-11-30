package com.zeejfps.sr;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Bitmap {

    public final int width;
    public final int height;
    public final int[] pixels;
    public final boolean isOpaque;

    private Bitmap(int width, int height, int[] pixels, boolean isOpaque) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
        this.isOpaque = isOpaque;
        if (!isOpaque) {
            for (int i = 0; i < pixels.length; i++) {
                int src = pixels[i];

                int a = (src>>24) & 0xff;
                float alpha = a / 255f;

                int r = (int)(((src>>16) & 0xff) * alpha);
                int g = (int)(((src>>8) & 0xff) * alpha);
                int b = (int)(((src) & 0xff) * alpha);

                pixels[i] = a << 24 | r << 16 | g << 8 | b;
            }
        }
    }

    public static Bitmap create(int width, int height, boolean isOpaque) {
        return new Bitmap(width, height, new int[width*height], isOpaque);
    }

    public static Bitmap attach(BufferedImage img, boolean isOpaque) {
        int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        return new Bitmap(img.getWidth(), img.getHeight(), pixels, isOpaque);
    }
}
