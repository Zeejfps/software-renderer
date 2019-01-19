package com.zeejfps.sr;

import org.joml.Vector2i;

public class Rasterizer3D extends Rasterizer {

    public Rasterizer3D(Bitmap raster) {
        super(raster);
    }

    private Vector2i viewportToRasterCoord(float x, float y) {
        Vector2i result = new Vector2i();

        float halfWidth = raster.width * 0.5f;
        float halfHeight = raster.height * 0.5f;

        result.x = (int)(halfWidth + halfWidth * x + 0.5f);
        result.y = (int)(halfHeight + halfHeight * y + 0.5f);
        return result;
    }

    /*
     * A lot of drawing code comes of this article https://fgiesen.wordpress.com/2013/02/06/the-barycentric-conspirac/
     */
    public void fillTriangle(
        float x0, float y0, int c0,
        float x1, float y1, int c1,
        float x2, float y2, int c2
    )
    {

        Vector2i v0 = viewportToRasterCoord(x0, y0);
        Vector2i v1 = viewportToRasterCoord(x1, y1);
        Vector2i v2 = viewportToRasterCoord(x2, y2);

        // Compute triangle bounding box
        int minX = min3(v0.x, v1.x, v2.x);
        int minY = min3(v0.y, v1.y, v2.y);
        int maxX = max3(v0.x, v1.x, v2.x);
        int maxY = max3(v0.y, v1.y, v2.y);

        // Clip against screen bounds
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, raster.width - 1);
        maxY = Math.min(maxY, raster.height - 1);

        // Triangle setup
        int A01 = v0.y - v1.y, B01 = v1.x - v0.x;
        int A12 = v1.y - v2.y, B12 = v2.x - v1.x;
        int A20 = v2.y - v0.y, B20 = v0.x - v2.x;

        // Barycentric coordinates at minX/minY corner
        Vector2i p = new Vector2i(minX, minY);
        int w0_row = edge(v1, v2, p);
        int w1_row = edge(v2, v0, p);
        int w2_row = edge(v0, v1, p);

        float area = edge(v0, v1, v2); // area of the triangle multiplied by 2
        if (area == 0)
            return;

        // Rasterize
        for (p.y = minY; p.y <= maxY; p.y++) {

            // To save on a couple multiplications
            int index = minX + p.y * raster.width;

            // Barycentric coordinates at start of row
            int w0 = w0_row;
            int w1 = w1_row;
            int w2 = w2_row;

            for (p.x = minX; p.x <= maxX; p.x++, index++) {
                // Determine barycentric coordinates

                // If p is on or inside all edges, render pixel.
                if ((w0 | w1 | w2) >= 0) {

                    float wr = w0 / area;
                    float wg = w1 / area;
                    float wb = w2 / area;

                    int r = (int)(wr * ((c0 & 0xff0000) >> 16) + wg * ((c1 & 0xff0000) >> 16) + wb * ((c2 & 0xff0000) >> 16));
                    int g = (int)(wr * ((c0 & 0x00ff00) >>  8) + wg * ((c1 & 0x00ff00) >>  8) + wb * ((c2 & 0x00ff00) >>  8));
                    int b = (int)(wr * ((c0 & 0x0000ff)) + wg * ((c1 & 0x0000ff)) + wb * ((c2 & 0x0000ff)));
                    int color = (r << 16) | (g << 8) | b;
                    renderPixel(p.x + p.y * raster.width, color);
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

    private int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b,c));
    }

    private int min3(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    private int edge(Vector2i a, Vector2i b, Vector2i c) {
        return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
    }

}
