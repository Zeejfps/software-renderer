package com.zeejfps.sr;

import org.joml.Vector3d;

public class Mesh {

    private final Vertex[] vertices;
    private final int[] indecies;

    public Mesh(Vertex[] vertices, int[] indecies) {
        this.vertices = vertices;
        this.indecies = indecies;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int[] getIndecies() {
        return indecies;
    }

}