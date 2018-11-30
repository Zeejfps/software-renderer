package com.zeejfps.sr;

import com.zeejfps.sr.math.Vec2f;

public class Vertex {

    public final Vec2f position;
    public int color;

    public Vertex(float x, float y, int color) {
        this(new Vec2f(x, y), color);
    }

    public Vertex(Vec2f position, int color) {
        this.position = position;
        this.color = color;
    }

}
