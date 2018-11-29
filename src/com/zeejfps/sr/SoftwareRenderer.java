package com.zeejfps.sr;

import com.zeejfps.sr.impl.AwtApplication;

public class SoftwareRenderer implements ApplicationListener {

    private Application application;
    private Rasterizer rasterizer;

    public SoftwareRenderer() {
        application = new AwtApplication(1280, 720, 1280, 720);
        rasterizer = application.getRasterizer();
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();
    }

    int fps = 0;
    double startTime = 0;
    @Override
    public void update(double dt) {
        //rasterizer.drawLine(0.2f, 0.25f, 0.5f, 0.25f,0xff00ff);

    }

    @Override
    public void fixedUpdate() {
        rasterizer.clearColorBuffer(0xfff200);
        rasterizer.drawTriangle(0.5f, 0.1f, 0.3f, 0.9f, 0.8f, 0.56f, 0xff00ff);
        fps++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            System.out.println("FPS: " + fps);
            fps = 0;
            startTime = System.currentTimeMillis();
        }
    }

    public void run() {
        application.launch(this);
    }

    public static void main(String[] args) {
        new SoftwareRenderer().run();
    }

}
