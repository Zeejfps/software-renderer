package com.zeejfps.sr.rasterizer;

import com.zeejfps.sr.ZMath;

import java.util.Arrays;

public class Raster3D extends Raster {

    protected float[] depthBuffer;

    public Raster3D(int width, int height) {
        super(width, height);
        depthBuffer = new float[this.width * this.height];
        Arrays.fill(depthBuffer, Float.MAX_VALUE);
    }

    public float[] getDepthBuffer() {
        return depthBuffer;
    }

    public void drawTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        drawLine(x0, y0, x1, y1, color);
        drawLine(x1, y1, x2, y2, color);
        drawLine(x2, y2, x0, y0, color);
    }

    /*
     * A lot of drawing code comes of this article https://fgiesen.wordpress.com/2013/02/06/the-barycentric-conspirac/
     */
    public void fillTriangleFast(int x0, int y0, int c0, int x1, int y1, int c1, int x2, int y2, int c2) {
        // area of the triangle multiplied by 2
        float area = edge(x0, y0, x1, y1, x2, y2);
        if (area == 0)
            return;

        // Compute triangle bounding box
        int minX = ZMath.min3(x0, x1, x2);
        int minY = ZMath.min3(y0, y1, y2);
        int maxX = ZMath.max3(x0, x1, x2);
        int maxY = ZMath.max3(y0, y1, y2);

        // Clip against screen bounds
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, this.width - 1);
        maxY = Math.min(maxY, this.height - 1);

        // Triangle setup
        int A01 = y0 - y1, B01 = x1 - x0;
        int A12 = y1 - y2, B12 = x2 - x1;
        int A20 = y2 - y0, B20 = x0 - x2;

        // Barycentric coordinates at minX/minY corner
        int w0_row = edge(x1, y1, x2, y2, minX, minY);
        int w1_row = edge(x2, y2, x0, y0, minX, minY);
        int w2_row = edge(x0, y0, x1, y1, minX, minY);

        // Rasterize
        for (int i = minY; i <= maxY; i++) {

            // To save on a couple multiplications
            int index = minX + i * this.width;

            // Barycentric coordinates at start of row
            int w0 = w0_row;
            int w1 = w1_row;
            int w2 = w2_row;

            for (int j = minX; j <= maxX; j++, index++) {

                // If p is on or inside all edges, render pixel.
                if ((w0 | w1 | w2) >= 0) {

                    float wr = w0 / area;
                    float wg = w1 / area;
                    float wb = w2 / area;

                    int r = (int)(wr * ((c0 & 0xff0000) >> 16) + wg * ((c1 & 0xff0000) >> 16) + wb * ((c2 & 0xff0000) >> 16));
                    int g = (int)(wr * ((c0 & 0x00ff00) >>  8) + wg * ((c1 & 0x00ff00) >>  8) + wb * ((c2 & 0x00ff00) >>  8));
                    int b = (int)(wr * ((c0 & 0x0000ff)) + wg * ((c1 & 0x0000ff)) + wb * ((c2 & 0x0000ff)));
                    int color = (r << 16) | (g << 8) | b;
                    this.colorBuffer[index] = color;
                }

                // One step to the right
                w0 += A12;
                w1 += A20;
                w2 += A01;
            }

            // One row step
            w0_row += B12;
            w1_row += B20;
            w2_row += B01;
        }

    }

    public void fillTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {

        int vx0 = x0;
        int vy0 = y0;

        int vx1 = x1;
        int vy1 = y1;

        int vx2 = x2;
        int vy2 = y2;

        int temp;
        if (vy1 < vy0) {
            temp = vy1;
            vy1 = vy0;
            vy0 = temp;
        }

        if (vy2 < vy1) {
            temp = vy1;
            vy1 = vy2;
            vy2 = temp;
        }

        if (vy1 < vy0) {
            temp = vy1;
            vy1 = vy0;
            vy0 = temp;
        }

        // Check for flat top
        if (vy0 == vy1) {
            if (vx1 < vx0) {
                temp = vx1;
                vx1 = vx0;
                vx0 = temp;
            }
            fillFlatTopTri(vx0, vy0, vx1, vy1, vx2, vy2, color);
        }
        else if (vy1 == vy2) {
            if (vx2 < vx1) {
                temp = vy2;
                vy2 = vy1;
                vy1 = temp;
            }
            fillFlatBottomTri(vx0, vy0, vx1, vy1, vx2, vy2, color);
        }
        else {

            float a = (vy1 - vy0) / (float)(vy2 - vy0);

            int vix = (int)(vx0 + (vx2 - vx0) * a + 0.5f);
            int viy = (int)(vy0 + (vy2 - vy0) * a + 0.5f);

            if (vx1 < vix) {
                fillFlatBottomTri(vx0, vy0, vx1, vy1, vix, viy, color);
                fillFlatTopTri(vx1, vy1, vix, viy, vx2, vy2, color);
            }
            else {
                fillFlatBottomTri(vx0, vy0, vix, viy, vx1, vy1, color);
                fillFlatTopTri(vix, viy, vx1, vy1, vx2, vy2, color);
            }

        }

    }

    private int edge(int x0, int y0, int x1, int y1, int x2, int y2) {
        return (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
    }

    private void fillFlatBottomTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        System.out.println("Drawing flat bottom tri");
    }

    private void fillFlatTopTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        System.out.println("Drawing flat top tri");
    }

}
