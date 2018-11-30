package com.zeejfps.sr;

public abstract class Application {

    private static final double NS_PER_SECOND = 1000000000.0;
    private static final double NS_PER_UPDATE = NS_PER_SECOND / 60.0;
    private static final int MAX_FRAMES_SKIP = 5;

    private boolean running;
    private ApplicationListener appListener;

    public void launch(ApplicationListener appListener) {
        if (running) return;

        this.appListener = appListener;
        new Thread(() -> {
            running = true;
            loop();
        }).start();
    }

    public void terminate() {
        if (!running) return;
        running = false;
    }

    private int skippedFrames;
    private double lag = 0, current, elapsed, previous;
    private void loop() {
        previous = System.nanoTime();

        appListener.init();
        while(running) {
            update();
        }
    }

    public abstract Rasterizer getRasterizer();

    protected void update() {
        current = System.nanoTime();
        elapsed = current - previous;

        previous = current;
        lag += elapsed;

        while (lag >= NS_PER_UPDATE && skippedFrames < MAX_FRAMES_SKIP) {
            appListener.update();
            lag -= NS_PER_UPDATE;
            skippedFrames++;
        }
        skippedFrames = 0;
        appListener.render();
    }
}
