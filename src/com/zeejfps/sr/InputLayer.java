package com.zeejfps.sr;

import com.google.common.eventbus.Subscribe;
import com.zeejfps.sr.events.KeyPressedEvent;
import com.zeejfps.sr.events.KeyReleasedEvent;
import com.zeejfps.sr.events.MouseMoveEvent;

import java.awt.*;
import java.util.Arrays;

public class InputLayer {

    private boolean[] keyPressed = new boolean[256];
    private boolean[] keyReleased = new boolean[256];

    private int mouseX;
    private int mouseY;
    private int mouseDeltaX;
    private int mouseDeltaY;

    public boolean isKeyDown(int keyCode) {
        return keyPressed[keyCode];
    }

    public int getMouseDeltaX() {
        return mouseDeltaX;
    }

    public int getMouseDeltaY() {
        return mouseDeltaY;
    }

    public void reset() {
        //Arrays.fill(keyPressed, false);
        Arrays.fill(keyReleased, false);
        mouseDeltaX = 0;
        mouseDeltaY = 0;
    }

    @Subscribe private void onKeyPressed(KeyPressedEvent event) {
        keyPressed[event.getKeyCode()] = true;
    }

    @Subscribe private void onKeyReleased(KeyReleasedEvent event) {
        keyPressed[event.getKeyCode()] = false;
        keyReleased[event.getKeyCode()] = true;
    }

    @Subscribe private void onMouseMoved(MouseMoveEvent event) {
        mouseDeltaX = event.getDeltaX();
        mouseDeltaY = event.getDeltaY();
        mouseX = event.getX();
        mouseY = event.getY();
    }

}
