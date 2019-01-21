package com.zeejfps.sr;

import org.joml.Vector3d;
import org.joml.Vector4d;

public class Vertex {

    public final Vector3d position;
    public final int color;

    public Vertex(Vector3d position, int color) {
        this(position.x, position.y, position.z, color);
    }

    public Vertex(double x, double y, double z, int color) {
        this.position = new Vector3d(x, y, z);
        this.color = color;
    }


}
