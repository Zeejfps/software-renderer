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
        if (x < 0) {
            width += x;
            x = 0;
        }

        if (y < 0) {
            height += y;
            y = 0;
        }

        if (x+width > raster.width) {
            width = raster.width - x;
        }

        if (y + height > raster.height) {
            height = raster.height - y;
        }

        for (int i = 0; i < height; i++) {
            int rasterIndex = x + (y+i)*raster.width;
            for (int j = 0; j < width; j++) {
                raster.pixels[rasterIndex] = color;
                rasterIndex++;
            }
        }
    }

    public void drawHorizontalLine(int x, int y, int width, int color) {
        if (y < 0 || y >= raster.height)
            return;

        if (x < 0) {
            width += x;
            x = 0;
        }

        if (x + width > raster.width) {
            width = raster.width - x;
        }

        int rasterIndex = x + raster.width * y;
        for (int i = 0; i < width; i++) {
            raster.pixels[rasterIndex] = color;
            rasterIndex++;
        }
    }

    public void drawVerticalLine(int x, int y, int height, int color) {
        if (x < 0 || x >= raster.width)
            return;

        if (y < 0) {
            height += y;
            y = 0;
        }

        if (y + height > raster.height) {
            height = raster.height - y;
        }

        int rasterIndex = x + raster.width * y;
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
            int d = 0;

            int dx = Math.abs(endX - startX);
            int dy = Math.abs(endY - startY);

            int dx2 = 2 * dx; // slope scaling factors to
            int dy2 = 2 * dy; // avoid floating point

            int ix = startX < endX ? 1 : -1; // increment direction
            int iy = startY < endY ? 1 : -1;

            int x = startX;
            int y = startY;

            if (dx >= dy) {
                for (; x < endX; x += ix) {
                    drawPixel(x, y, color);
                    d += dy2;
                    if (d > dx) {
                        y += iy;
                        d -= dx2;
                    }
                }
            } else {
                for (; y < endY; y += iy) {
                    drawPixel(x, y, color);
                    d += dx2;
                    if (d > dy) {
                        x += ix;
                        d -= dy2;
                    }
                }
            }
        }
    }

    public void drawPixel(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= raster.width || y >= raster.height)
            return;
        raster.pixels[x + raster.width * y] = color;
    }

}
