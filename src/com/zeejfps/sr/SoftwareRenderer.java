package com.zeejfps.sr;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.Arrays;

public class SoftwareRenderer extends Application {

    private final AwtDisplay display;
    private final Rasterizer rasterizer;
    private final Camera camera;

    private final Bitmap colorBuffer;

    public SoftwareRenderer() {
        Config config = new Config();
        config.fullscreen = true;
        config.renderScale = 0.25f;
        display = new AwtDisplay(config);
        this.colorBuffer = Bitmap.attach(display.getFrameBuffer(), true);

        rasterizer = new SimpleRasterizer(this.colorBuffer);
        camera = new Camera(65f, (float)display.getWidth() / display.getHeight(), 0.01f, 100f);
    }

    @Override
    protected void init() {
        display.show();
        startTime = System.currentTimeMillis();
    }

    int fps = 0;
    double startTime = 0;

    private Vertex[] vertices = {
            new Vertex(0.5f, 0.1f, 1f, 0xff0000),
            new Vertex(0.8f, 0.5f, 1f, 0xff0000),
            new Vertex(0.2f, 0.9f, 1f, 0xff0000),
    };

    @Override
    public void render() {

        Arrays.fill(colorBuffer.pixels, 0x002233);
        renderTriangle(vertices[0], vertices[1], vertices[2]);

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
        );

        rasterizer.fillTriangle(
                -0.2f, -0.1f, 0xff00ff,
                0.23f, 1.2f, 0xff00ff,
                -0.2f, 0.9f, 0x3300ff
        );*/

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
        rotation += 0.1f;
    }

    /* Testing Stuff */
    float rotation = 40f;
    Quaternionf rotationQ = new Quaternionf();

    private void renderTriangle(Vertex v0, Vertex v1, Vertex v2) {

        Vector4f p0 = v0.position.mul(new Matrix4f().rotateZ(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix());
        Vector4f p1 = v1.position.mul(new Matrix4f().rotateZ(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix(), new Vector4f());
        Vector4f p2 = v2.position.mul(new Matrix4f().rotateZ(rotation), new Vector4f()).mulProject(camera.getViewProjMatrix(), new Vector4f());

        rasterizer.fillTriangle(
                p0.x, p0.y, v0.color,
                p1.x, p1.y, v1.color,
                p2.x, p2.y, v2.color
        );
    }

    public static void main(String[] args) {
        new SoftwareRenderer().launch();
    }
}
