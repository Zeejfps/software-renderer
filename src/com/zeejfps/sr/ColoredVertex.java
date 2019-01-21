package com.zeejfps.sr;

public class ColoredVertex extends Vertex {

    private int color;

    @Override
    public Vertex lerp(Vertex other, double factor, double w0, double w1) {
        return super.lerp(other, factor, w0, w1);
    }
}
