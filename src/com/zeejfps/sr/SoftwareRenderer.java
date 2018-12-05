package com.zeejfps.sr;

public class SoftwareRenderer implements ApplicationListener {

    private Application application;
    private Rasterizer renderer;

    public SoftwareRenderer() {
        application = new AwtApplication(1280, 720, 1280, 720);
        renderer = application.getRasterizer();
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();
    }

    int fps = 0;
    double startTime = 0;

    @Override
    public void render() {
        renderer.fillTriangle(
                0.5f, 0.1f, 0xff0000,
                0.8f, 0.5f, 0x00ff00,
                0.2f, 0.9f, 0x0000ff
        );

        renderer.fillTriangle(
                0f, -0.5f, 0xff00ff,
                0.5f, 0.5f, 0xff00ff,
                -0.3f, 0.7f, 0xff00ff
        );

        renderer.fillTriangle(
                -0.5f, -0.8f, 0xf430ff,
                0.1f, 0.3f, 0xff055f,
                -0.9f, 0.2f, 0xf230ff
        );

        renderer.fillTriangle(
                -0.2f, -0.1f, 0xff00ff,
                0.23f, 1.2f, 0xff00ff,
                -0.2f, 0.9f, 0x3300ff
        );


        fps++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            System.out.println("FPS: " + fps);
            fps = 0;
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void update() {

    }

    public void run() {
        application.launch(this);
    }

    public static void main(String[] args) {
        new SoftwareRenderer().run();



        int[] pixel = new int[1024 * 1024];
        pixel[1337] = 0xAAFFBBAA;


        int i = pixel[0];

    }

}
