package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster3D;
import com.zeejfps.sr.utils.OBJImporter;
import org.joml.*;

import java.io.IOException;
import java.lang.Math;
import java.util.Arrays;

public class SoftwareRenderer extends Application {

    private final AwtDisplay display;
    private final Raster3D raster;
    private final Camera camera;

    private Mesh car;

    public SoftwareRenderer() {
        Config config = new Config();
        config.fullscreen = false;
        config.renderScale = 0.9f;

        raster = new Raster3D(640, 480);

        display = new AwtDisplay(config, raster);

        camera = new Camera(65f, (float)display.getWidth() / display.getHeight(), 0.01f, 100f);

        try {
            car = OBJImporter.load("res/car.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        display.show();
        startTime = System.currentTimeMillis();
    }

    int fps = 0;
    double startTime = 0;

    private Vertex[] vertices = {
            new Vertex(0f, -0.6f, 0f, 0xff0000),
            new Vertex(0.8f, 0.5f, 0f, 0x00ff00),
            new Vertex(-0.8f, 0.5f, 0f, 0x0000ff),
    };

    @Override
    public void render() {

        Arrays.fill(raster.getColorBuffer(), 0x002233);

        //thisizer.drawHorizontalLine(2, 2, 2, 0xff00ff);
        //thisizer.drawVerticalLine(2, 2, 2, 0xff00ff);

        //thisizer.fillRect(100, 25, 400, 320, 0xff0000);

        //thisizer.drawRect(4, 4, 22, 22, 0xffff00);

        //thisizer.drawHorizontalLine(-15, 25, 10, 0xff0000);

        //thisizer.fillRect(5, 5, 20, 20, 0xff00ff);

        //thisizer.drawLine(40, 40, 41, 65, 0xff2233);

        //thisizer.drawLine(15, 10, 15, 40, 0xffff00);

        //thisizer.drawLine(25, 25, 180, 240, 0xffff00);

        raster.drawTri(10, 10, 50, 30, 15, 20, 0xff2244);

        raster.drawLine(20, 20, 5, 5, 0xff00ff);

        /*thisizer.fillTriangleFast(
                0.5f, 0.1f, 0xff0000,
                0.8f, 0.5f, 0x00ff00,
                0.2f, 0.9f, 0x0000ff
        );

        thisizer.fillTriangleFast(
                0f, -0.5f, 0xff00ff,
                0.5f, 0.5f, 0xff00ff,
                -0.3f, 0.7f, 0xff00ff
        );

        thisizer.fillTriangleFast(
                -0.5f, -0.8f, 0xf430ff,
                0.1f, 0.3f, 0xff055f,
                -0.9f, 0.2f, 0xf230ff
        );*/

        raster.fillTri(40, 40, 90, 40, 20, 185, 0xff00ff);

        raster.fillTri(120, 40, 90, 70, 100, 70, 0xff00ff);

        raster.fillTri(150, 155, 170, 160, 140, 140, 0xff2233);

       // renderTriangle(vertices[0], vertices[1], vertices[2]);
       // renderTriangle(vertices[0], vertices[2], vertices[1]);

        renderMesh(car);

        fps++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            System.out.println("FPS: " + fps);
            fps = 0;
            startTime = System.currentTimeMillis();
        }

        display.swapBuffers();
    }

    @Override
    public void update() {
        rotation += 0.02f;
    }

    /* Testing Stuff */
    float rotation = 0f;
    Quaternionf rotationQ = new Quaternionf();

    private void renderTriangle(Vertex v0, Vertex v1, Vertex v2) {

        Vector4f p0 = v0.position.mul(new Matrix4f().rotateY(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix());
        Vector4f p1 = v1.position.mul(new Matrix4f().rotateY(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix());
        Vector4f p2 = v2.position.mul(new Matrix4f().rotateY(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix());

        Vector2i vp0 = ndcToRasterCoord(p0.x, p0.y);
        Vector2i vp1 = ndcToRasterCoord(p1.x, p1.y);
        Vector2i vp2 = ndcToRasterCoord(p2.x, p2.y);

        raster.fillTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, 0xff00ff);
    }

    private Vector2i ndcToRasterCoord(float x, float y) {
        Vector2i result = new Vector2i();

        float halfWidth = raster.getWidth() * 0.5f;
        float halfHeight = raster.getHeight() * 0.5f;

        result.x = (int)(halfWidth + halfWidth * x + 0.5f);
        result.y = (int)(halfHeight + halfHeight * y + 0.5f);
        return result;
    }

    private void renderMesh(Mesh mesh) {
        Matrix4f transform = new Matrix4f();

        //Quaternion xRot = new Quaternion(Vector3.RIGHT,  mesh.rotation.x);
        //Quaternion yRot = new Quaternion(Vector3.UP,  mesh.rotation.y);
        //Quaternion zRot = new Quaternion(Vector3.FORWARD,  mesh.rotation.z);

        //Quaternionf rotation = new Quaternionf((mesh.rotation.y, mesh.rotation.z, mesh.rotation.x);//yRot.mult(xRot).mult(zRot);

        //Matrix4 worldMatrix = transform.mult(rotation);
        int[] indecies = mesh.getIndecies();

        /*Vector3f[] verts = new Vector3f[mesh.getVertices().length];
        for (int i = 0; i < verts.length; i++) {

            Vector3f v = rotation.mult(mesh.getVertices()[i]);
            verts[i] = screen.getMatrix().mult(camera.mult(transform)).mult(v);

        }*/

        for (int i = 0; i < mesh.getIndecies().length; i+=3) {

            Vector3f v0 = mesh.getVertices()[indecies[i]];
            Vector3f v1 = mesh.getVertices()[indecies[i+1]];
            Vector3f v2 = mesh.getVertices()[indecies[i+2]];

            Matrix3f r = new Matrix3f().rotationXYZ(0, rotation, (float)Math.toRadians(180.0));
            Vector3f p0 = v0.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());
            Vector3f p1 = v1.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());
            Vector3f p2 = v2.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());

            Vector2i vp0 = ndcToRasterCoord(p0.x, p0.y);
            Vector2i vp1 = ndcToRasterCoord(p1.x, p1.y);
            Vector2i vp2 = ndcToRasterCoord(p2.x, p2.y);

            raster.fillTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, 0xff00ff);
            raster.drawTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, 0x00ff23);
        }
    }

    public static void main(String[] args) {
        new SoftwareRenderer().launch();
    }
}
