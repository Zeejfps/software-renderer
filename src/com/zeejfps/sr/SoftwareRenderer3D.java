package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster3D;
import org.joml.Matrix4d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector4d;

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

    public void renderMesh(Mesh mesh, Transform transform) {

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

}
