package com.zeejfps.sr.rasterizer;

import com.zeejfps.sr.ZMath;

import java.util.Arrays;

public class Raster3D extends Raster {

    protected double[] depthBuffer;

    public Raster3D(int width, int height) {
        super(width, height);
        depthBuffer = new double[this.width * this.height];
        Arrays.fill(depthBuffer, Float.MAX_VALUE);
    }

    public void clearDepthBuffer() {
        Arrays.fill(depthBuffer, Float.MAX_VALUE);
    }

    public void drawTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        drawLine(x0, y0, x1, y1, color);
        drawLine(x1, y1, x2, y2, color);
        drawLine(x2, y2, x0, y0, color);
    }

    /*
     * A lot of drawing code comes of this article https://fgiesen.wordpress.com/2013/02/06/the-barycentric-conspirac/
     */
    public void fillTriFast(
            int x0, int y0, double z0,
            int x1, int y1, double z1,
            int x2, int y2, double z2, int color) {

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

        /*
        // Check 8x8 chunks
        boolean shouldDraw = false;
        int block = 8;

        for (int i = minY; i <= maxY - block; i+=block) {

            for (int j = minX; j <= maxX - block; j+=block) {

                // Get the coordinates for the top left most pixel
                int w0_tl = edge(x1, y1, x2, y2, j, i);
                int w1_tl = edge(x2, y2, x0, y0, j, i);
                int w2_tl = edge(x0, y0, x1, y1, j, i);

                shouldDraw |= (w0_tl | w1_tl | w2_tl) >= 0;

                int w0_tr = edge(x1, y1, x2, y2, j+block, i);
                int w1_tr = edge(x2, y2, x0, y0, j+block, i);
                int w2_tr = edge(x0, y0, x1, y1, j+block, i);

                shouldDraw |= (w0_tr | w1_tr | w2_tr) >=0;

                int w0_bl = edge(x1, y1, x2, y2, j, i+block);
                int w1_bl = edge(x2, y2, x0, y0, j, i+block);
                int w2_bl = edge(x0, y0, x1, y1, j, i+block);

                shouldDraw |= (w0_bl | w1_bl | w2_bl) >=0;

                int w0_br = edge(x1, y1, x2, y2, j+block, i+block);
                int w1_br = edge(x2, y2, x0, y0, j+block, i+block);
                int w2_br = edge(x0, y0, x1, y1, j+block, i+block);

                shouldDraw |= (w0_br | w1_br | w2_br) >=0;
            }

        }

        if (!shouldDraw)
            return;
        */

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

                // If p is on or inside all edges, onRender pixel.
                if ((w0 | w1 | w2) >= 0) {

                    double wr = w0 / area;
                    double wg = w1 / area;
                    double wb = w2 / area;

                    double z = z0 * wr + z1 * wg + z2 * wb;

                    if (z < depthBuffer[index]) {
                        depthBuffer[index] = z;
                        /*int r = (int)(wr * ((c0 & 0xff0000) >> 16) + wg * ((c1 & 0xff0000) >> 16) + wb * ((c2 & 0xff0000) >> 16));
                        int g = (int)(wr * ((c0 & 0x00ff00) >>  8) + wg * ((c1 & 0x00ff00) >>  8) + wb * ((c2 & 0x00ff00) >>  8));
                        int b = (int)(wr * ((c0 & 0x0000ff)) + wg * ((c1 & 0x0000ff)) + wb * ((c2 & 0x0000ff)));
                        int color = (r << 16) | (g << 8) | b;*/
                        this.colorBuffer[index] = color;
                    }
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

            temp = vx1;
            vx1 = vx0;
            vx0 = temp;
        }

        if (vy2 < vy1) {
            temp = vy1;
            vy1 = vy2;
            vy2 = temp;

            temp = vx1;
            vx1 = vx2;
            vx2 = temp;
        }

        if (vy1 < vy0) {
            temp = vy1;
            vy1 = vy0;
            vy0 = temp;

            temp = vx1;
            vx1 = vx0;
            vx0 = temp;
        }

        // Check for flat top
        if (vy0 == vy1) {
            if (vx1 < vx0) {
                temp = vx1;
                vx1 = vx0;
                vx0 = temp;

                temp = vy1;
                vy1 = vy0;
                vy0 = temp;
            }
            fillFlatTopTri(vx0, vy0, vx1, vy1, vx2, vy2, color);
        }
        else if (vy1 == vy2) {
            if (vx2 < vx1) {
                temp = vy2;
                vy2 = vy1;
                vy1 = temp;

                temp = vx2;
                vx2 = vx1;
                vx1 = temp;
            }
            fillFlatBottomTri(vx0, vy0, vx1, vy1, vx2, vy2, color);
        }
        else {

            float a = (vy1 - vy0) / (float)(vy2 - vy0);

            int vx4 = (int)(vx0 +  a * (vx2 - vx0));
            int vy4 = vy1;

            if (vx1 < vx4) {
                fillFlatBottomTri(vx0, vy0, vx1, vy1, vx4, vy4, color);
                fillFlatTopTri(vx1, vy1, vx4, vy4, vx2, vy2, color);
            }
            else {
                fillFlatBottomTri(vx0, vy0, vx4, vy4, vx1, vy1, color);
                fillFlatTopTri(vx4, vy4, vx1, vy1, vx2, vy2, color);
            }

        }

    }

    private int edge(int x0, int y0, int x1, int y1, int x2, int y2) {
        return (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
    }

    private void fillFlatBottomTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {

        float invslope1 = (x1 - x0) / (float)(y1 - y0);
        float invslope2 = (x2 - x0) / (float)(y2 - y0);

        float curx1 = x0;
        float curx2 = x0;

        for (int scanlineY = y0; scanlineY <= y1; scanlineY++)
        {
            drawLine((int)curx1, scanlineY, (int)curx2, scanlineY, color);
            curx1 += invslope1;
            curx2 += invslope2;
        }

        /*double m0 = (x1 - x0) / (double)(y1 - y0);
        double m1 = (x2 - x0) / (double)(y2 - y0);

        int ys = (int) Math.ceil(y0 - 0.5);
        int ye = (int) Math.ceil(y2 - 0.5);

        for (int y = ys; y < ye; y++) {

            double px0 = m0 * (y + 0.5 - y0) + x0;
            double px1 = m1 * (y + 0.5 - y0) + x0;

            int xs = (int) Math.ceil(px0 - 0.5);
            int xe = (int) Math.ceil(px1 - 0.5);

            int index = xs + y * width;
            for (int x = xs; x < xe; x++, index++) {
                colorBuffer[index] = color;
            }

        }*/

    }

    private void fillFlatTopTri(int x0, int y0, int x1, int y1, int x2, int y2, int color) {

        float invslope1 = (x2 - x0) / (float)(y2 - y0);
        float invslope2 = (x2 - x1) / (float)(y2 - y1);

        float curx1 = x2;
        float curx2 = x2;

        for (int scanlineY = y2; scanlineY > y0; scanlineY--)
        {
            drawLine((int)curx1, scanlineY, (int)curx2, scanlineY, color);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }

        /*double m0 = (x2 - x0) / (double)(y2 - y0);
        double m1 = (x2 - x1) / (double)(y2 - y1);

        int ys = (int) Math.ceil(y0 - 0.5);
        int ye = (int) Math.ceil(y2 - 0.5);

        for (int y = ys; y <= ye; y++) {

            double px0 = m0 * (y + 0.5 - y0) + x0;
            double px1 = m1 * (y + 0.5 - y1) + x1;

            int xs = (int) Math.ceil(px0 - 0.5);
            int xe = (int) Math.ceil(px1 - 0.5);

            int index = xs + y * width;
            for (int x = xs; x < xe; x++, index++) {
                colorBuffer[index] = color;
            }

        }*/

    }

}
