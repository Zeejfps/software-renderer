package com.zeejfps.sr.math;

public class Point2D {

    public int x, y;

    public Point2D() {
        this(0, 0);
    }

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D copy) {
        this(copy.x, copy.y);
    }

    public void set(Point2D v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point2D[" + x + ", " + y + "]";
    }

}
