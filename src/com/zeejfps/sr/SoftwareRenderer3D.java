package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster;
import com.zeejfps.sr.rasterizer.Raster3D;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SoftwareRenderer3D {

    private final Raster3D raster;

    public SoftwareRenderer3D(Raster3D raster) {
        this.raster = raster;
    }

    public void clear(int color) {
        raster.clearColorBuffer(color);
        raster.clearDepthBuffer();
    }

    public void renderMesh(Matrix4d mvp, Mesh mesh) {

        int[] indecies = mesh.getIndecies();
        Vertex[] vertices = mesh.getVertices();

        for (int i = 0; i < indecies.length-2; i+=3) {

            Triangle triangleTriangle = new Triangle(
                vertices[indecies[i + 0]],
                vertices[indecies[i + 1]],
                vertices[indecies[i + 2]]
            );

            triangleTriangle.toClipSpace(mvp);

            List<Triangle> clippedFaced = clip(triangleTriangle);
            for (Triangle triangle : clippedFaced) {

                triangle.perspectiveDivide();

                if (canCull(triangle))
                    continue;

                Vector4d vtx0 = triangle.v0.p;
                Vector4d vtx1 = triangle.v1.p;
                Vector4d vtx2 = triangle.v2.p;

                triangle.toScreenSpace(raster);

                raster.fillTriFast(
                    (int)vtx0.x, (int)vtx0.y, vtx0.z, 0xff0000,
                    (int)vtx1.x, (int)vtx1.y, vtx1.z, 0x00ff00,
                    (int)vtx2.x, (int)vtx2.y, vtx2.z, 0xff00ff
                );
            }
        }

    }

    public void renderMeshIntStream(Matrix4d mvp, Mesh mesh) {

        int[] indecies = mesh.getIndecies();
        Vertex[] vertices = mesh.getVertices();

        IntStream.iterate(0, i -> i + 3)
                .parallel()
                .limit(indecies.length/3)
                .forEach((i) -> {

                    Triangle triangleTriangle = new Triangle(
                            vertices[indecies[i + 0]],
                            vertices[indecies[i + 1]],
                            vertices[indecies[i + 2]]
                    );

                    triangleTriangle.toClipSpace(mvp);

                    List<Triangle> clippedFaced = clip(triangleTriangle);
                    for (Triangle triangle : clippedFaced) {

                        triangle.perspectiveDivide();

                        if (canCull(triangle))
                            continue;

                        Vector4d vtx0 = triangle.v0.p;
                        Vector4d vtx1 = triangle.v1.p;
                        Vector4d vtx2 = triangle.v2.p;

                        triangle.toScreenSpace(raster);

                        raster.fillTriFast(
                                (int)vtx0.x, (int)vtx0.y, vtx0.z, 0xff0000,
                                (int)vtx1.x, (int)vtx1.y, vtx1.z, 0x00ff00,
                                (int)vtx2.x, (int)vtx2.y, vtx2.z, 0x0000ff
                        );
                    }

                });

    }

    private List<Triangle> clip(Triangle triangle) {
        List<Triangle> triangles = new ArrayList<>();

        Vertex vtx0 = triangle.v0;
        Vertex vtx1 = triangle.v1;
        Vertex vtx2 = triangle.v2;

        if (vtx0.p.w <= 0 && vtx1.p.w <= 0 && vtx2.p.w <= 0)
            return triangles;

        if (vtx0.p.w > 0 && vtx1.p.w > 0 && vtx2.p.w > 0 &&
                Math.abs(vtx0.p.z) < vtx0.p.w &&
                Math.abs(vtx1.p.z) < vtx1.p.w &&
                Math.abs(vtx2.p.z) < vtx2.p.w
        ){
            triangles.add(triangle);
            return triangles;
        }

        List<Vertex> vertices = new ArrayList<>();
        clipEdge(vtx0, vtx1, vertices);
        clipEdge(vtx1, vtx2, vertices);
        clipEdge(vtx2, vtx0, vertices);

        if (vertices.size() < 3) {
            return triangles;
        }

        if (vertices.get(vertices.size()-1).equals(vertices.get(0)))
            vertices.remove(vertices.size() - 1);

        for (int i = 1; i < vertices.size() - 1; i++) {
            triangles.add(new Triangle(
               vertices.get(0),
               vertices.get(i),
               vertices.get(i + 1)
            ));
        }

        return triangles;
    }

    private void clipEdge(Vertex v0, Vertex v1, List<Vertex> verticies) {

        Vertex v0New = v0;
        Vertex v1New = v1;

        boolean v0Inside = v0.p.w > 0 && v0.p.z > -v0.p.w;
        boolean v1Inside = v1.p.w > 0 && v1.p.z > -v1.p.w;

        if (v0Inside && v1Inside) {
            // Inside near plane
        }
        else if (v0Inside || v1Inside) {

            double d0 = v0.p.z + v0.p.w;
            double d1 = v1.p.z + v1.p.w;

            double factor = 1.0 / (d1 - d0);

            Vertex v = v0.lerp(v1, factor, d0, d1);

            if (v0Inside)
                v1New = v;
            else
                v0New = v;

        }
        else {
            return;
        }

        if (verticies.size() == 0 || !verticies.get(verticies.size()-1).equals(v0New)) {
            verticies.add(v0New);
        }

        verticies.add(v1New);
    }

    private boolean canCull(Triangle triangle) {
        double d = (triangle.v1.p.x - triangle.v0.p.x) *
                   (triangle.v2.p.y - triangle.v0.p.y) -
                   (triangle.v1.p.y - triangle.v0.p.y) *
                   (triangle.v2.p.x - triangle.v0.p.x);
        return d < 0.0;
    }

    private static class Triangle {

        public final Vertex v0;
        public final Vertex v1;
        public final Vertex v2;

        public Triangle(Vertex v0, Vertex v1, Vertex v2) {
            this.v0 = Vertex.copyOf(v0);
            this.v1 = Vertex.copyOf(v1);
            this.v2 = Vertex.copyOf(v2);
        }

        public void toClipSpace(Matrix4d mvp) {
            v0.p.mul(mvp);
            v1.p.mul(mvp);
            v2.p.mul(mvp);
        }

        public void perspectiveDivide() {
            v0.p.x /= v0.p.w;
            v0.p.y /= v0.p.w;
            v0.p.z /= v0.p.w;

            v1.p.x /= v1.p.w;
            v1.p.y /= v1.p.w;
            v1.p.z /= v1.p.w;

            v2.p.x /= v2.p.w;
            v2.p.y /= v2.p.w;
            v2.p.z /= v2.p.w;
        }

        public void toScreenSpace(Raster raster) {
            double halfWidth = raster.getWidth() * 0.5;
            double halfHeight = raster.getHeight() * 0.5;

            v0.p.x = Math.floor(halfWidth * (v0.p.x + 1));
            v0.p.y = Math.floor(halfHeight * (v0.p.y + 1));

            v1.p.x = Math.floor(halfWidth * (v1.p.x + 1));
            v1.p.y = Math.floor(halfHeight * (v1.p.y + 1));

            v2.p.x = Math.floor(halfWidth * (v2.p.x + 1));
            v2.p.y = Math.floor(halfHeight * (v2.p.y + 1));
        }
    }

}
