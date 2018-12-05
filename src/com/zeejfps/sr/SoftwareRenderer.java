package com.zeejfps.sr;

public class SoftwareRenderer extends AwtApplication {

    @Override
    protected void setup(Config config) {

    }

    @Override
    protected void init() {
        startTime = System.currentTimeMillis();
    }

    int fps = 0;
    double startTime = 0;

    @Override
    public void render() {
        rasterizer.fillTriangle(
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

    public static void main(String[] args) {
        new SoftwareRenderer().launch();
    }
}
