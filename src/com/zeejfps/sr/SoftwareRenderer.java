package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Bitmap;
import com.zeejfps.sr.rasterizer.Rasterizer3D;
import com.zeejfps.sr.utils.OBJImporter;
import org.joml.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;

public class SoftwareRenderer extends Application {

    private final AwtDisplay display;
    private final Rasterizer3D rasterizer;
    private final Camera camera;

    private final Bitmap colorBuffer;

    private Mesh car;

    public SoftwareRenderer() {
        Config config = new Config();
        config.fullscreen = false;
        config.renderScale = 0.45f;
        display = new AwtDisplay(config);
        BufferedImage img = display.getFrameBuffer();
        int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        this.colorBuffer = Bitmap.of(pixels, img.getWidth(), img.getHeight());

        rasterizer = new Rasterizer3D(this.colorBuffer);
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

        Arrays.fill(colorBuffer.pixels, 0x002233);

        //rasterizer.drawHorizontalLine(2, 2, 2, 0xff00ff);
        //rasterizer.drawVerticalLine(2, 2, 2, 0xff00ff);

        rasterizer.drawRect(1, 1, 20, 20, 0xff0000);

        rasterizer.fillRect(5, 5, 20, 20, 0xff00ff);

        /*rasterizer.fillTriangle(
                0.5f, 0.1f, 0xff0000,
                0.8f, 0.5f, 0x00ff00,
                0.2f, 0.9f, 0x0000ff
        );

        rasterizer.fillTriangle(
                0f, -0.5f, 0xff00ff,
                0.5f, 0.5f, 0xff00ff,
                -0.3f, 0.7f, 0xff00ff
        );

        rasterizer.fillTriangle(
                -0.5f, -0.8f, 0xf430ff,
                0.1f, 0.3f, 0xff055f,
                -0.9f, 0.2f, 0xf230ff
        );*/

        rasterizer.fillTriangle(
                -0.2f, -0.1f, 0xff00ff,
                0.23f, 1.2f, 0xff00ff,
                -0.2f, 0.9f, 0x3300ff
        );

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
        Vector4f p1 = v1.position.mul(new Matrix4f().rotateY(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix(), new Vector4f());
        Vector4f p2 = v2.position.mul(new Matrix4f().rotateY(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix(), new Vector4f());

        rasterizer.fillTriangle(
                p0.x, p0.y, v0.color,
                p1.x, p1.y, v1.color,
                p2.x, p2.y, v2.color
        );
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

            Matrix3f r = new Matrix3f().rotationXYZ(rotation, rotation, rotation);
            Vector3f p0 = v0.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());
            Vector3f p1 = v1.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());
            Vector3f p2 = v2.mul(r, new Vector3f()).mulProject(camera.getViewProjMatrix());

            rasterizer.fillTriangle(
                    p0.x, p0.y, 0xff0000,
                    p1.x, p1.y, 0x00ff00,
                    p2.x, p2.y, 0x0000ff
            );
        }
    }

    public static void main(String[] args) {
        new SoftwareRenderer().launch();
    }
}
