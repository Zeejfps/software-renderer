package com.zeejfps.sr;

import org.joml.Vector3d;

public class Mesh {

    private final Vector3d[] vertices;
    private final int[] indecies;

    public Mesh(Vector3d[] vertices, int[] indecies) {
        this.vertices = vertices;
        this.indecies = indecies;
    }

    public Vector3d[] getVertices() {
        return vertices;
    }

    public int[] getIndecies() {
        return indecies;
    }

}