package com.zeejfps.sr;

public class Rasterizer {

    protected Bitmap raster;

    public Rasterizer(Bitmap raster) {
        this.raster = raster;
    }

    public void setRaster(Bitmap bitmap) {
        this.raster = raster;
    }

    public Bitmap getRaster() {
        return raster;
    }

    protected void renderPixel(int index, int color) {
        raster.pixels[index] = color;
    }

}
