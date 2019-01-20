package com.zeejfps.sr.events;

public class KeyEvent {

    private final int keyCode;

    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

}
