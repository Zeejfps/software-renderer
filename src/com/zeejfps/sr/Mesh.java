package com.zeejfps.sr;

import org.joml.Vector3f;

public class Mesh {

    private final Vector3f[] vertices;
    private final int[] indecies;

    public Mesh(Vector3f[] vertices, int[] indecies) {
        this.vertices = vertices;
        this.indecies = indecies;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public int[] getIndecies() {
        return indecies;
    }

}