package com.zeejfps.sr;

public interface Rasterizer {
    void fillTriangle(float x0, float y0, int c1,
                      float x1, float y1, int c2,
                      float x2, float y2, int c3);
}
