package com.zeejfps.sr.events;

public class MouseMoveEvent extends MouseEvent {

    private int deltaX, deltaY;

    public MouseMoveEvent(int x, int y, int deltaX, int deltaY) {
        super(x, y);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }
}
