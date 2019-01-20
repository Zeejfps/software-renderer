package com.zeejfps.sr.events;

public class KeyPressedEvent extends KeyEvent {

    private final boolean repeated;

    public KeyPressedEvent(int keyCode, boolean repeated) {
        super(keyCode);
        this.repeated = repeated;
    }

    public boolean isRepeated() {
        return repeated;
    }
}
