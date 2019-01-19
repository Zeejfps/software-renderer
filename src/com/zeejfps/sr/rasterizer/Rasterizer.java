package com.zeejfps.sr.rasterizer;

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

    public void drawCircle(int centerX, int centerY, int radius, int color) {

    }

    public void fillCircle(int centerX, int centerY, int radius, int color) {

    }

    public void drawRect(int x, int y, int width, int height, int color) {
        drawHorizontalLine(x, y, width , color);
        drawHorizontalLine(x, y + height-1, width, color);
        drawVerticalLine(x, y, height, color);
        drawVerticalLine(x + width-1, y, height, color);
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        for (int i = 0; i < height; i++) {
            int rasterIndex = x + (y+i)*raster.width;
            for (int j = 0; j < width; j++) {
                raster.pixels[rasterIndex] = color;
                rasterIndex++;
            }
        }
    }

    public void drawHorizontalLine(int startX, int startY, int width, int color) {
        int rasterIndex = startX + raster.width * startY;
        for (int i = 0; i < width; i++) {
            raster.pixels[rasterIndex] = color;
            rasterIndex++;
        }
    }

    public void drawVerticalLine(int startX, int startY, int height, int color) {
        int rasterIndex = startX + raster.width * startY;
        for (int i = 0; i < height; i++) {
            raster.pixels[rasterIndex] = color;
            rasterIndex += raster.width;
        }
    }

    public void drawLine(int startX, int startY, int endX, int endY, int color) {
        if (startX == endX){
            drawVerticalLine(startX, startY, endY - endX, color);
        }
        else if (startY == endY) {
            drawHorizontalLine(startX, startY, endX - startX, color);
        }
        else {

            int dx = endX - startX;
            int dy = endY - startY;
            int d = 2 * dy - dx;
            int y = startY;

            for (int x = startX; x <= endX; x++) {
                drawPixel(x, y, color);
                if (d > 0) {
                    y += 1;
                    d -= 2 * dx;
                }
                d += 2 * dy;
            }
        }
    }

    public void drawPixel(int x, int y, int color) {
        raster.pixels[x + raster.width * y] = color;
    }

}