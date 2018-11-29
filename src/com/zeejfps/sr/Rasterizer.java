package com.zeejfps.sr;

public interface Rasterizer {
    void clearColorBuffer(int color);
    void drawLine(float x0, float y0, float x1, float y1, int color);
    void drawTriangle(float x0, float y0, float x1, float y1, float x2, float y2, int color);
}
