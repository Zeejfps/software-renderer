package com.zeejfps.sr.events;

public class MouseEvent extends Event {

    private int x, y;

    public MouseEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
