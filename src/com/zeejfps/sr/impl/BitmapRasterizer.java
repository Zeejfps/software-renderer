package com.zeejfps.sr.impl;

import com.zeejfps.sr.Rasterizer;
import com.zeejfps.sr.math.Vec2i;

import java.util.Arrays;

public class BitmapRasterizer implements Rasterizer {

    private final Bitmap colorBuffer;

    public BitmapRasterizer(Bitmap colorBuffer) {
        this.colorBuffer = colorBuffer;
    }

    @Override
    public void clearColorBuffer(int color) {
        Arrays.fill(colorBuffer.pixels, color);
    }

    @Override
    public void drawLine(float x0, float y0, float x1, float y1, int color) {
        Vec2i s = viewportToScreenCoords(x0, y0);
        Vec2i e = viewportToScreenCoords(x1, y1);
        drawLine(s.x, s.y, e.x, e.y, color);
    }

    private Vec2i viewportToScreenCoords(float x, float y) {
        Vec2i v = new Vec2i();
        v.x = (int)(x * colorBuffer.width);
        v.y = (int)(y * colorBuffer.height);
        return v;
    }

    private void drawLine(int x0, int y0, int x1, int y1, int color) {
        if (y0 == y1) {
            drawHorizontalLine(x0, x1, y0, color);
        }
        else
        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1)
                drawLineLow(x1, y1, x0, y0, color);
            else
                drawLineLow(x0, y0, x1, y1, color);
        }
        else {
            if (y0 > y1)
                drawLineHigh(x1, y1, x0, y0, color);
            else
                drawLineHigh(x0, y0, x1, y1, color);
        }
    }

    @Override
    public void drawTriangle(float x0, float y0, float x1, float y1, float x2, float y2, int color) {
        Vec2i v0 = viewportToScreenCoords(x0, y0);
        Vec2i v1 = viewportToScreenCoords(x1, y1);
        Vec2i v2 = viewportToScreenCoords(x2, y2);

        if (v0.y > v2.y) {
            swap(v0, v2);
        }

        if (v0.y > v1.y) {
            swap(v0, v1);
        }

        if (v1.y > v2.y) {
            swap(v1, v2);
        }

        drawTriangle(v0.x, v0.y, v1.x, v1.y, v2.x, v2.y, color);
    }

    private void drawTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {

        if (y1 == y2) {
            drawBottomFlatTriangle(x0, y0, x1, y1, x2, y2, color);
        }
        else if (y0 == y1) {
            drawTopFlatTriangle(x0, y0, x1, y1, x2, y2, color);
        }
        else {
            int x3 = (int)(x0 + ((float)(y1 - y0) / (float)(y2 - y0)) * (x2 - x0));
            drawBottomFlatTriangle(x0, y0, x1, y1, x3, y1, color);
            drawTopFlatTriangle(x1, y1, x3, y1, x2, y2, color);
        }
    }

    private void drawBottomFlatTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color)
    {
        float invslope1 = (float)(x1 - x0) / (float)(y1 - y0);
        float invslope2 = (float)(x2 - x0) / (float)(y2 - y0);

        float curx1 = x0;
        float curx2 = x0;

        for (int scanlineY = y0; scanlineY <= y1; scanlineY++)
        {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, color);
            curx1 += invslope1;
            curx2 += invslope2;
        }
    }

    private void drawTopFlatTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color)
    {
        float invslope1 = (x2 - x0) / (float)(y2 - y0);
        float invslope2 = (x2 - x1) / (float)(y2- y1);

        float curx1 = x2;
        float curx2 = x2;

        for (int scanlineY = y2; scanlineY > y0; scanlineY--)
        {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, color);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }
    }

    private void swap(Vec2i v0, Vec2i v1) {
        Vec2i temp = new Vec2i(v1);
        v1.set(v0);
        v0.set(temp);
    }

    private void drawLineLow(int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if  (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int d = 2*dy - dx;
        int y = y0;

        for (int x = x0; x <= x1; x++) {
            setPixel(x, y, color);
            if (d > 0) {
                y = y + yi;
                d = d - 2*dx;
            }
            d = d + 2*dy;
        }
    }

    private void drawLineHigh(int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if  (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int d = 2*dx - dy;
        int x = x0;

        for (int y = y0; y <= y1; y++) {
            setPixel(x, y, color);
            if (d > 0) {
                x = x + xi;
                d = d - 2*dy;
            }
            d = d + 2*dx;
        }
    }

    private void drawHorizontalLine(int x0, int x1, int y, int color) {
        if (y < 0 || y >= colorBuffer.height)
            return;
        if (x0 < 0 || x0 >= colorBuffer.width)
            return;
        if (x1 < 0 || x1 >= colorBuffer.width)
            return;
        int xs = x0 + colorBuffer.width * y;
        int xe = x1 + colorBuffer.width * y;
        if (xe > xs)
            Arrays.fill(colorBuffer.pixels, xs, xe, color);
        else
            Arrays.fill(colorBuffer.pixels, xe, xs, color);
    }

    private void setPixel(int x, int y, int color) {
        if (y < 0 || y >= colorBuffer.height)
            return;
        if (x < 0 || x >= colorBuffer.width)
            return;
        colorBuffer.pixels[x + y * colorBuffer.width] = color;
    }
}
