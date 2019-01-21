package com.zeejfps.sr;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.rasterizer.Raster3D;
import com.zeejfps.sr.utils.OBJImporter;
import org.joml.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.Math;

public class SoftwareRenderer extends Application {

    private final AwtDisplay display;
    private final Raster3D raster;
    private final Camera camera;

    private EventBus eventBus = new EventBus();
    private InputLayer inputLayer = new InputLayer();
    private AwtEventDispatcher awtEventDispatcher;

    private Mesh car, cube;

    public SoftwareRenderer() {
        Config config = new Config();
        config.windowWidth = 640;
        config.windowHeight = 480;
        config.fullscreen = false;
        config.renderScale = 1f;

        eventBus.register(inputLayer);

        raster = new Raster3D(640, 480);

        display = new AwtDisplay(config, raster);

        awtEventDispatcher = new AwtEventDispatcher(eventBus, display.getComponent());

        camera = new Camera(90f, (float)display.getWidth() / display.getHeight(), 0.01f, 1000f);

        try {
            car = OBJImporter.load("res/Skotizo.obj");
            cube = OBJImporter.load("res/cube.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        display.show();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void fixedUpdate() {

    }

    int fps = 0;
    double startTime = 0;

    private Vertex[] vertices = {
            new Vertex(0f, -0.6f, 0f, 0xff0000),
            new Vertex(0.8f, 0.5f, 0f, 0x00ff00),
            new Vertex(-0.8f, 0.5f, 0f, 0x0000ff),
    };

    Transform carTransform = new Transform();

    Grid grid = new Grid();

    @Override
    public void render() {

        raster.clearColorBuffer(0x002233);
        raster.clearDepthBuffer();

        //thisizer.drawHorizontalLine(2, 2, 2, 0xff00ff);
        //thisizer.drawVerticalLine(2, 2, 2, 0xff00ff);

        //thisizer.fillRect(100, 25, 400, 320, 0xff0000);

        //thisizer.drawRect(4, 4, 22, 22, 0xffff00);

        //thisizer.drawHorizontalLine(-15, 25, 10, 0xff0000);

        //thisizer.fillRect(5, 5, 20, 20, 0xff00ff);

        //thisizer.drawLine(40, 40, 41, 65, 0xff2233);

        //thisizer.drawLine(15, 10, 15, 40, 0xffff00);

        //thisizer.drawLine(25, 25, 180, 240, 0xffff00);

//        raster.drawTri(10, 10, 50, 30, 15, 20, 0xff2244);
//
//        raster.drawLine(20, 20, 5, 5, 0xff00ff);

        /*thisizer.fillTriFast(
                0.5f, 0.1f, 0xff0000,
                0.8f, 0.5f, 0x00ff00,
                0.2f, 0.9f, 0x0000ff
        );

        thisizer.fillTriFast(
                0f, -0.5f, 0xff00ff,
                0.5f, 0.5f, 0xff00ff,
                -0.3f, 0.7f, 0xff00ff
        );

        thisizer.fillTriFast(
                -0.5f, -0.8f, 0xf430ff,
                0.1f, 0.3f, 0xff055f,
                -0.9f, 0.2f, 0xf230ff
        );*/

//        raster.fillTri(40, 40, 90, 40, 20, 185, 0xff00ff);
//
//        raster.fillTri(120, 40, 90, 70, 100, 70, 0xff00ff);
//
//        raster.fillTri(150, 155, 170, 160, 140, 140, 0xff2233);

       // renderTriangle(vertices[0], vertices[1], vertices[2]);
       // renderTriangle(vertices[0], vertices[2], vertices[1]);

        //carTransform.position.z = -10f;
        //carTransform.position.y = -5f;
        carTransform.rotation.y = rotation;
        //camera.transform.position.z -= rotation;
        //renderMesh(car, carTransform);

        //grid.render(camera, raster);

        renderMesh(cube, carTransform);

        fps++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            System.out.println("FPS: " + fps);
            fps = 0;
            startTime = System.currentTimeMillis();
        }

        display.swapBuffers();
    }

    @Override
    public void update(double dt) {
        rotation += dt * 0.55;

        if (inputLayer.isKeyDown(KeyEvent.VK_Q))
            camera.forward.rotateY((float)dt * 5f);
        else if (inputLayer.isKeyDown(KeyEvent.VK_E))
            camera.forward.rotateY((float)dt * -5f);

        if (inputLayer.isKeyDown(KeyEvent.VK_W))
            camera.transform.position.add(camera.forward.mul((float)dt * 5f, new Vector3f()));
        else if (inputLayer.isKeyDown(KeyEvent.VK_S))
            camera.transform.position.sub(camera.forward.mul((float)dt * 5f, new Vector3f()));

        if (inputLayer.isKeyDown(KeyEvent.VK_A))
            camera.transform.position.x -= 5 * dt;
        else if (inputLayer.isKeyDown(KeyEvent.VK_D))
            camera.transform.position.x += 5 * dt;

        if (inputLayer.isKeyDown(KeyEvent.VK_X)) {
            camera.transform.position.y -= 5.0 * dt;
        }
        else if (inputLayer.isKeyDown(KeyEvent.VK_Z)) {
            camera.transform.position.y += 5.0 * dt;
        }

        inputLayer.reset();
    }

    /* Testing Stuff */
    float rotation = 0f;

    private Vector2i ndcToRasterCoord(float x, float y) {
        Vector2i result = new Vector2i();

        float halfWidth = raster.getWidth() * 0.5f;
        float halfHeight = raster.getHeight() * 0.5f;

        result.x = (int)(halfWidth * (x + 1f) + 0.5f);
        result.y = (int)(halfHeight * (y + 1f) + 0.5f);
        return result;
    }

    private void renderMesh(Mesh mesh, Transform transform) {

        int[] indecies = mesh.getIndecies();

        Vector3f light = new Vector3f(0, 0, -1f).normalize();

        for (int i = 0; i < mesh.getIndecies().length; i+=3) {

            Vector3f vtx0 = mesh.getVertices()[indecies[i]];
            Vector3f vtx1 = mesh.getVertices()[indecies[i+1]];
            Vector3f vtx2 = mesh.getVertices()[indecies[i+2]];

            Matrix4f transformMatrix = transform.getTransformationMatrix();

            // Local -> World
            Vector4f p0 = new Vector4f(vtx0, 1f).mul(transformMatrix);
            Vector4f p1 = new Vector4f(vtx1, 1f).mul(transformMatrix);
            Vector4f p2 = new Vector4f(vtx2, 1f).mul(transformMatrix);

            // World -> View
            Matrix4f viewMatrix = camera.getViewMatrix();
            Vector4f v0 = p0.mul(viewMatrix, new Vector4f());
            Vector4f v1 = p1.mul(viewMatrix, new Vector4f());
            Vector4f v2 = p2.mul(viewMatrix, new Vector4f());

            Vector3f line1 = new Vector3f(v1.x, v1.y, v1.z).sub(new Vector3f(v0.x, v0.y, v0.z));
            Vector3f line2 = new Vector3f(v2.x, v2.y, v2.z).sub(new Vector3f(v0.x, v0.y, v0.z));
            Vector3f normal = line1.cross(line2, new Vector3f()).normalize();
            float v = normal.dot(new Vector3f(v0.x, v0.y, v0.z));
            if (v > 0) {
                continue;
            }

            float lum = normal.dot(light);
            if (lum < 0) lum = -lum;

            int r = (int)Math.ceil(0xF0 * lum + 0.5);
            int g = (int)Math.ceil(0x01 * lum + 0.5);
            int b = (int)Math.ceil(0x25 * lum + 0.5);

            int color = ((r&0xff)<<16)|((g&0xff)<<8)|(b&0xff);

            Matrix4f projMatrix = camera.getProjMatrix();
            Vector4f pr0 = v0.mulProject(projMatrix, new Vector4f());
            Vector4f pr1 = v1.mulProject(projMatrix, new Vector4f());
            Vector4f pr2 = v2.mulProject(projMatrix, new Vector4f());

            Vector2i vp0 = ndcToRasterCoord(pr0.x, pr0.y);
            Vector2i vp1 = ndcToRasterCoord(pr1.x, pr1.y);
            Vector2i vp2 = ndcToRasterCoord(pr2.x, pr2.y);

//            raster.fillTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, color);
            raster.fillTriFast(vp0.x, vp0.y, p0.z, color, vp1.x, vp1.y, p1.z, color, vp2.x, vp2.y, p2.z, color);
            //raster.drawTri(vp0.x, vp0.y, vp1.x, vp1.y, vp2.x, vp2.y, 0x233ff3);
        }
    }

    public static void main(String[] args) {
        new SoftwareRenderer().launch();
    }
}
