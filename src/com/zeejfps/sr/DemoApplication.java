package com.zeejfps.sr;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.awt.AwtDisplay;
import com.zeejfps.sr.rasterizer.Raster3D;
import com.zeejfps.sr.utils.OBJImporter;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class DemoApplication extends Application {

    public static final int WINDOW_WIDTH = 720;
    public static final int WINDOW_HEIGHT = 500;
    public static final float RENDER_SCALE = 0.55f;

    private Raster3D raster;
    private EventBus eventBus;
    private AwtDisplay display;
    private SoftwareRenderer3D renderer;

    /* Testing Stuff */
    Mesh bunnyMesh, cubeMesh, planeMesh;
    Transform bunnyTransform, cubeTransform, planeTransform;
    double rotation;
    int fps = 0;
    double startTime = 0;
    Camera camera;
    InputLayer inputLayer;
    double yaw, pitch;

    @Override
    protected void onCreate() {
        eventBus = new EventBus();

        raster = new Raster3D(
                (int)(WINDOW_WIDTH * RENDER_SCALE),
                (int)(WINDOW_HEIGHT * RENDER_SCALE)
        );

        display = new AwtDisplay.Builder()
                .withWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT)
                .containCursor()
                .hideCursor()
                //.setFullscreen()
                .buildAndShow(eventBus, raster);

        camera = new Camera(65, WINDOW_WIDTH / (double)WINDOW_HEIGHT, 0.1, 100);
        renderer = new SoftwareRenderer3D(raster);

        inputLayer  = new InputLayer();
        eventBus.register(inputLayer);

        try {
            bunnyMesh = OBJImporter.load("res/bunny.obj");
            planeMesh = OBJImporter.load("res/plane.obj");
            cubeMesh = OBJImporter.load("res/cube.obj");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        bunnyTransform = new Transform();
        bunnyTransform.position.y = -4;
        planeTransform = new Transform();
        cubeTransform = new Transform();
        cubeTransform.position.y = -1;

        camera.transform.position.x = 10;
        camera.transform.position.z = -20;
        camera.transform.position.y = -5;
    }

    @Override
    protected void onFixedUpdate() {

    }

    @Override
    protected void onUpdate(double dt) {
        rotation += dt * 0.55;
        bunnyTransform.rotation.y = rotation;

        pitch += (float)dt * 55 * inputLayer.getMouseDeltaY();

//        if(pitch > 80.0f)
//            pitch = 80.0f;
//        if(pitch < -80.0f)
//            pitch = -80.0f;

        yaw += (float)dt * 55 * inputLayer.getMouseDeltaX();

        camera.forward.x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        camera.forward.y = Math.sin(Math.toRadians(pitch));
        camera.forward.z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        camera.forward.normalize();

        if (inputLayer.isKeyDown(KeyEvent.VK_W))
            camera.transform.position.add(camera.forward.mul(dt * 5f, new Vector3d()));
        else if (inputLayer.isKeyDown(KeyEvent.VK_S))
            camera.transform.position.sub(camera.forward.mul(dt * 5f, new Vector3d()));

        if (inputLayer.isKeyDown(KeyEvent.VK_A)) {

            Vector3d temp = new Vector3d();
            camera.forward.cross(camera.up, temp).normalize();
            temp.mul((float)dt * 5);

            camera.transform.position.sub(temp);
        }
        else if (inputLayer.isKeyDown(KeyEvent.VK_D)) {

            Vector3d temp = new Vector3d();
            camera.forward.cross(camera.up, temp).normalize();
            temp.mul((float) dt * 5);

            camera.transform.position.add(temp);
        }
        if (inputLayer.isKeyDown(KeyEvent.VK_X)) {
            camera.transform.position.y -= 5.0 * dt;
        }
        else if (inputLayer.isKeyDown(KeyEvent.VK_Z)) {
            camera.transform.position.y += 5.0 * dt;
        }

        inputLayer.reset();
    }

    @Override
    protected void onRender() {
        renderer.clear(0x002233);

        Matrix4d vp = new Matrix4d(camera.getProjMatrix()).mul(camera.getViewMatrix());

        Matrix4d mvp = new Matrix4d();

        mvp.identity().mul(vp).mul(bunnyTransform.getModelMatrix());
        renderer.renderMeshIntStream(mvp, bunnyMesh);

        mvp.identity().mul(vp).mul(cubeTransform.getModelMatrix());
        renderer.renderMeshIntStream(mvp, cubeMesh);

        mvp.identity().mul(vp).mul(planeTransform.getModelMatrix());
        renderer.renderMeshIntStream(mvp, planeMesh);

        display.swapBuffers();

        fps++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            System.out.println("FPS: " + fps);
            fps = 0;
            startTime = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {
        new DemoApplication().launch();
    }

}
