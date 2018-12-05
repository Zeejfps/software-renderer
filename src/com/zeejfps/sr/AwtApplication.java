package com.zeejfps.sr;

import java.util.Arrays;

public abstract class AwtApplication extends Application {

    private Config config;

    public final AwtDisplay display;

    public final Bitmap colorBuffer;

    public AwtApplication() {
        config = new Config();
        this.setup(config);

        display = new AwtDisplay(config);
        this.colorBuffer = Bitmap.attach(display.getBuffer(), true);
    }

    @Override
    public void launch() {
        display.show();
        super.launch();
    }

    @Override
    protected void tick() {
        Arrays.fill(colorBuffer.pixels, 0x002233);
        super.tick();
        display.swapBuffers();
    }

    protected abstract void setup(Config config);

}
