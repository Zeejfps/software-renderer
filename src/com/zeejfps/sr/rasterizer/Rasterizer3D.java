package com.zeejfps.sr.rasterizer;

import com.zeejfps.sr.ZMath;
import org.joml.Vector2i;

public class Rasterizer3D extends Rasterizer {

    public Rasterizer3D(Bitmap raster) {
        super(raster);
    }

    public Vector2i viewportToRasterCoord(float x, float y) {
        Vector2i result = new Vector2i();

        float halfWidth = raster.width * 0.5f;
        float halfHeight = raster.height * 0.5f;

        result.x = (int)(halfWidth + halfWidth * x + 0.5f);
        result.y = (int)(halfHeight + halfHeight * y + 0.5f);
        return result;
    }

    public void fillTriangle(
        float x0, float y0, int c0,
        float x1, float y1, int c1,
        float x2, float y2, int c2
    ) {
        Vector2i v0 = viewportToRasterCoord(x0, y0);
        Vector2i v1 = viewportToRasterCoord(x1, y1);
        Vector2i v2 = viewportToRasterCoord(x2, y2);

        fillTriangle(v0.x, v0.y, c0, v1.x, v1.y, c1, v2.x, v2.y, c2);
    }

    /*
     * A lot of drawing code comes of this article https://fgiesen.wordpress.com/2013/02/06/the-barycentric-conspirac/
     */
    public void fillTriangle(int x0, int y0, int c0, int x1, int y1, int c1, int x2, int y2, int c2)
    {
        // Compute triangle bounding box
        int minX = ZMath.min3(x0, x1, x2);
        int minY = ZMath.min3(y0, y1, y2);
        int maxX = ZMath.max3(x0, x1, x2);
        int maxY = ZMath.max3(y0, y1, y2);

        // Clip against screen bounds
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, raster.width - 1);
        maxY = Math.min(maxY, raster.height - 1);

        // Triangle setup
        int A01 = y0 - y1, B01 = x1 - x0;
        int A12 = y1 - y2, B12 = x2 - x1;
        int A20 = y2 - y0, B20 = x0 - x2;

        // Barycentric coordinates at minX/minY corner
        int w0_row = edge(x1, y1, x2, y2, minX, minY);
        int w1_row = edge(x2, y2, x0, y0, minX, minY);
        int w2_row = edge(x0, y0, x1, y1, minX, minY);

        float area = edge(x0, y0, x1, y1, x2, y2); // area of the triangle multiplied by 2
        if (area == 0)
            return;

        // Rasterize
        for (int i = minY; i <= maxY; i++) {

            // To save on a couple multiplications
            int index = minX + i * raster.width;

            // Barycentric coordinates at start of row
            int w0 = w0_row;
            int w1 = w1_row;
            int w2 = w2_row;

            for (int j = minX; j <= maxX; j++, index++) {
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
                    raster.pixels[index] = color;
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

    private int edge(int x0, int y0, int x1, int y1, int x2, int y2) {
        return (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
    }

}
