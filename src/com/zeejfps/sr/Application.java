package com.zeejfps.sr;

public abstract class Application {

    private static final double NS_PER_SECOND = 1000000000.0;
    private static final double NS_PER_UPDATE = NS_PER_SECOND / 60.0;
    private static final int MAX_FRAMES_SKIP = 5;

    private boolean running;

    public void launch() {
        if (running) return;

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

        onCreate();
        while(running) {
            this.tick();
        }
    }

    protected void tick() {
        current = System.nanoTime();
        elapsed = current - previous;

        previous = current;
        lag += elapsed;

        while (lag >= NS_PER_UPDATE && skippedFrames < MAX_FRAMES_SKIP) {
            onFixedUpdate();
            lag -= NS_PER_UPDATE;
            skippedFrames++;
        }
        skippedFrames = 0;
        onUpdate(elapsed / NS_PER_SECOND);
        onRender();
    }

    protected abstract void onCreate();

    protected abstract void onFixedUpdate();

    protected abstract void onUpdate(double dt);

    protected abstract void onRender();
}
