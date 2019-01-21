package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster;
import com.zeejfps.sr.rasterizer.Raster3D;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class SoftwareRenderer3D {

    private final Raster3D raster;
    private final Camera camera;

    public SoftwareRenderer3D(Raster3D raster, Camera camera) {
        this.raster = raster;
        this.camera = camera;
    }

    public void clear(int color) {
        raster.clearColorBuffer(color);
        raster.clearDepthBuffer();
    }

    public void renderMesh(Matrix4d mvp, Mesh mesh) {

        int[] indecies = mesh.getIndecies();
        Vector3d[] vertices = mesh.getVertices();

        for (int i = 0; i < indecies.length - 3; i+=3) {

            Face triangleFace = new Face(
                new Vector4d(vertices[indecies[i + 0]], 1),
                new Vector4d(vertices[indecies[i + 1]], 1),
                new Vector4d(vertices[indecies[i + 2]], 1)
            );

            triangleFace.toClipSpace(mvp);

            List<Face> clippedFaced = clip(triangleFace);
            for (Face face : clippedFaced) {

                /*Vector3d line1 = new Vector3d(v1.x, v1.y, v1.z).sub(new Vector3d(vtx0.x, vtx0.y, vtx0.z));
                Vector3d line2 = new Vector3d(v2.x, v2.y, v2.z).sub(new Vector3d(vtx0.x, vtx0.y, vtx0.z));
                Vector3d normal = line1.cross(line2, new Vector3d()).normalize();*/

                face.perspectiveDivide();

                if (canCull(face))
                    continue;

                face.toScreenSpace(raster);

                Vector4d vtx0 = face.vertices[0];
                Vector4d vtx1 = face.vertices[1];
                Vector4d vtx2 = face.vertices[2];

                raster.fillTriFast(
                    (int)vtx0.x, (int)vtx0.y, vtx0.z, 0xff00ff,
                    (int)vtx1.x, (int)vtx1.y, vtx1.z, 0xff00ff,
                    (int)vtx2.x, (int)vtx2.y, vtx2.z, 0xff00ff
                );
            }
        }

    }

    public void renderMeshOld(Mesh mesh, Transform transform) {

        int[] indecies = mesh.getIndecies();

        Vector3d light = new Vector3d(0, 0, -1f).normalize();

        for (int i = 0; i < mesh.getIndecies().length; i+=3) {

            Vector3d vtx0 = mesh.getVertices()[indecies[i]];
            Vector3d vtx1 = mesh.getVertices()[indecies[i+1]];
            Vector3d vtx2 = mesh.getVertices()[indecies[i+2]];

            Matrix4d transformMatrix = transform.getTransformationMatrix();

            // Local -> World
            Vector4d p0 = new Vector4d(vtx0, 1f).mul(transformMatrix);
            Vector4d p1 = new Vector4d(vtx1, 1f).mul(transformMatrix);
            Vector4d p2 = new Vector4d(vtx2, 1f).mul(transformMatrix);

            // World -> View
            Matrix4d viewMatrix = camera.getViewMatrix();
            Vector4d v0 = p0.mul(viewMatrix, new Vector4d());
            Vector4d v1 = p1.mul(viewMatrix, new Vector4d());
            Vector4d v2 = p2.mul(viewMatrix, new Vector4d());

            Vector3d line1 = new Vector3d(v1.x, v1.y, v1.z).sub(new Vector3d(v0.x, v0.y, v0.z));
            Vector3d line2 = new Vector3d(v2.x, v2.y, v2.z).sub(new Vector3d(v0.x, v0.y, v0.z));
            Vector3d normal = line1.cross(line2, new Vector3d()).normalize();
            double v = normal.dot(new Vector3d(v0.x, v0.y, v0.z));
            if (v > 0) {
                continue;
            }

            double lum = normal.dot(light);
            if (lum < 0) lum = -lum;

            int r = (int)Math.ceil(0xF0 * lum + 0.5);
            int g = (int)Math.ceil(0x01 * lum + 0.5);
            int b = (int)Math.ceil(0x25 * lum + 0.5);

            int color = ((r&0xff)<<16)|((g&0xff)<<8)|(b&0xff);

            Matrix4d projMatrix = camera.getProjMatrix();
            Vector4d pr0 = v0.mulProject(projMatrix, new Vector4d());
            Vector4d pr1 = v1.mulProject(projMatrix, new Vector4d());
            Vector4d pr2 = v2.mulProject(projMatrix, new Vector4d());

            Vector2i vp0 = ndcToRasterCoord(pr0.x, pr0.y);
            Vector2i vp1 = ndcToRasterCoord(pr1.x, pr1.y);
            Vector2i vp2 = ndcToRasterCoord(pr2.x, pr2.y);

//            raster.fillTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, color);
            raster.fillTriFast(vp0.x, vp0.y, pr0.z, color, vp1.x, vp1.y, pr1.z, color, vp2.x, vp2.y, pr2.z, color);
            //raster.drawTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, 0x233ff3);
        }
    }

    private Vector2i ndcToRasterCoord(double x, double y) {
        Vector2i result = new Vector2i();

        double halfWidth = raster.getWidth() * 0.5f;
        double halfHeight = raster.getHeight() * 0.5f;

        result.x = (int)(halfWidth * (x + 1f) + 0.5f);
        result.y = (int)(halfHeight * (y + 1f) + 0.5f);
        return result;
    }

    private List<Face> clip(Face face) {
        List<Face> faces = new ArrayList<>();

        Vector4d vtx0 = face.vertices[0];
        Vector4d vtx1 = face.vertices[1];
        Vector4d vtx2 = face.vertices[2];

        if (vtx0.w <= 0 && vtx1.w <= 0 && vtx2.w <= 0)
            return faces;

        if (vtx0.w > 0 && vtx1.w > 0 && vtx2.w > 0 &&
                Math.abs(vtx0.z) < vtx0.w &&
                Math.abs(vtx1.z) < vtx1.w &&
                Math.abs(vtx2.z) < vtx2.w
        ){
            faces.add(face);
            return faces;
        }

        List<Vector4d> vertices = new ArrayList<>();
        clipEdge(vtx0, vtx1, vertices);
        clipEdge(vtx1, vtx2, vertices);
        clipEdge(vtx2, vtx0, vertices);

        if (vertices.size() < 3)
            return faces;

        for (int i = 0; i < vertices.size() - 1; i++) {
            faces.add(new Face(
               vertices.get(0),
               vertices.get(i),
               vertices.get(i + 1)
            ));
        }

        return faces;
    }

    private void clipEdge(Vector4d v0, Vector4d v1, List<Vector4d> verticeis) {

    }

    private boolean canCull(Face face) {
        return false;
    }

    private static class Face {
        public final Vector4d[] vertices;
        public Face(Vector4d... vertices) {
            this.vertices = vertices;
        }

        public void toClipSpace(Matrix4d mvp) {
            for (Vector4d v : vertices)
                v.mul(mvp);
        }

        public void perspectiveDivide() {
            for (Vector4d v : vertices) {
                v.x /= v.w;
                v.y /= v.w;
                v.z /= v.w;
            }
        }

        public void toScreenSpace(Raster raster) {
            for (Vector4d v : vertices) {
                v.x = Math.floor(0.5 * raster.getWidth() * (v.x + 1));
                v.y = Math.floor(0.5 * raster.getHeight() * (v.y + 1));
            }
        }
    }

}
