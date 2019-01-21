package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster;
import com.zeejfps.sr.rasterizer.Raster3D;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Grid {

    private final Line[] lines = {
        new Line(
                new Vector3f(-0.5f, -0.5f, 0f),
                new Vector3f(0.5f, -0.5f, 0f)
        ),
        new Line(
                new Vector3f(-0.5f, 0f, 0f),
                new Vector3f(0.5f, 0f, 0f)
        ),
        new Line(
                new Vector3f(-0.5f, 0.5f, 0f),
                new Vector3f(0.5f, 0.5f, 0f)
        ),
        new Line(
                new Vector3f(-0.5f, -0.5f, 0f),
                new Vector3f(-0.5f, 0.5f, 0f)
        ),
        new Line(
                new Vector3f(0.5f, -0.5f, 0f),
                new Vector3f(0.5f, 0.5f, 0f)
        ),
        new Line(
                new Vector3f(0f, -0.5f, 0f),
                new Vector3f(0f, 0.5f, 0f)
        ),
    };

    public void render(Camera camera, Raster3D raster) {

        for (Line line : lines) {

            Vector3f sp = line.start.mulProject(camera.getViewProjMatrix(), new Vector3f());
            Vector3f ep = line.end.mulProject(camera.getViewProjMatrix(), new Vector3f());

            Vector2i p0 = ndcToRasterCoord(raster, sp.x, sp.y);
            Vector2i p1 = ndcToRasterCoord(raster, ep.x, ep.y);


            raster.drawLine(p0.x, p0.y, p1.x, p1.y, 0xff00ff);
        }

    }

    private Vector2i ndcToRasterCoord(Raster raster, float x, float y) {
        Vector2i result = new Vector2i();

        float halfWidth = raster.getWidth() * 0.5f;
        float halfHeight = raster.getHeight() * 0.5f;

        result.x = (int)(halfWidth * (x + 1f) + 0.5f);
        result.y = (int)(halfHeight * (y + 1f) + 0.5f);
        return result;
    }

    private static class Line {
        public final Vector3f start, end;
        public Line(Vector3f start, Vector3f end) {
            this.start = start;
            this.end = end;
        }
    }
}
