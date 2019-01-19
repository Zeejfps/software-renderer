package com.zeejfps.sr.rasterizer;

public class Raster {

    protected int[] colorBuffer;
    protected int width;
    protected int height;

    public Raster(int width, int height) {
        this(new int[width * height], width, height);
    }

    protected Raster(int[] pixels, int width, int height) {
        this.colorBuffer = pixels;
        this.width = width;
        this.height = height;
    }

    public int[] getColorBuffer() {
        return colorBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

        if (x+width > this.width) {
            width = this.width - x;
        }

        if (y + height > this.height) {
            height = this.height - y;
        }

        for (int i = 0; i < height; i++) {
            int thisIndex = x + (y+i)*this.width;
            for (int j = 0; j < width; j++) {
                this.colorBuffer[thisIndex] = color;
                thisIndex++;
            }
        }
    }

    protected void drawHorizontalLine(int x, int y, int width, int color) {
        if (y < 0 || y >= this.height)
            return;

        if (x < 0) {
            width += x;
            x = 0;
        }

        if (x + width > this.width) {
            width = this.width - x;
        }

        int thisIndex = x + this.width * y;
        for (int i = 0; i < width; i++) {
            this.colorBuffer[thisIndex] = color;
            thisIndex++;
        }
    }

    protected void drawVerticalLine(int x, int y, int height, int color) {
        if (x < 0 || x >= this.width)
            return;

        if (y < 0) {
            height += y;
            y = 0;
        }

        if (y + height > this.height) {
            height = this.height - y;
        }

        int thisIndex = x + this.width * y;
        for (int i = 0; i < height; i++) {
            this.colorBuffer[thisIndex] = color;
            thisIndex += this.width;
        }
    }

    public void drawLine(int startX, int startY, int endX, int endY, int color) {
        if (startX == endX){
            drawVerticalLine(startX, startY, endY - startY, color);
        }
        else if (startY == endY) {
            drawHorizontalLine(startX, startY, endX - startX, color);
        }
        else {

            if (startX < 0)
                startX = 0;

            if (endX > this.width)
                endX = this.width;

            if (startY < 0)
                startY = 0;

            if (endY > this.height)
                endY = this.height;

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
                for (; x != endX; x += ix) {
                    this.colorBuffer[x + y * this.width] = color;
                    d += dy2;
                    if (d > dx) {
                        y += iy;
                        d -= dx2;
                    }
                }
            } else {
                for (; y != endY; y += iy) {
                    this.colorBuffer[x + y * this.width] = color;
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
        if (x < 0 || y < 0 || x >= this.width || y >= this.height)
            return;
        this.colorBuffer[x + this.width * y] = color;
    }

}
