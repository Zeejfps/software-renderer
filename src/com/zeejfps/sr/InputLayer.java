package com.zeejfps.sr;

import com.google.common.eventbus.Subscribe;
import com.zeejfps.sr.events.KeyPressedEvent;
import com.zeejfps.sr.events.KeyReleasedEvent;

import java.util.Arrays;

public class InputLayer {

    private boolean[] keyPressed = new boolean[256];
    private boolean[] keyReleased = new boolean[256];

    @Subscribe private void onKeyPressed(KeyPressedEvent event) {
        keyPressed[event.getKeyCode()] = true;
    }

    @Subscribe private void onKeyReleased(KeyReleasedEvent event) {
        keyPressed[event.getKeyCode()] = false;
        keyReleased[event.getKeyCode()] = true;
    }

    public boolean isKeyDown(int keyCode) {
        return keyPressed[keyCode];
    }

    public void reset() {
        //Arrays.fill(keyPressed, false);
        Arrays.fill(keyReleased, false);
    }

}
