package com.zeejfps.sr;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Vertex {

    public final Vector4f position;
    public final int color;

    public Vertex(Vector3f position, int color) {
        this(position.x, position.y, position.z, color);
    }

    public Vertex(float x, float y, float z, int color) {
        this.position = new Vector4f(x, y, z, 1f);
        this.color = color;
    }


}
