package com.zeejfps.sr.math;

public class Vec2i {

    public int x, y;

    public Vec2i() {
        this(0, 0);
    }

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(Vec2i copy) {
        this(copy.x, copy.y);
    }

    public void set(Vec2i v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vec2i[" + x + ", " + y + "]";
    }

}
