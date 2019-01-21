package com.zeejfps.sr;

import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.Objects;

public class Vertex {

    public final Vector4d p;

    public Vertex() {
        p = new Vector4d(0, 0, 0, 1);
    }

    public Vertex(Vector3d p) {
        this(p.x, p.y, p.z);
    }

    public Vertex(double x, double y, double z) {
        this.p = new Vector4d(x, y, z, 1);
    }

    private Vertex(Vector4d pos) {
        this.p = pos;
    }

    public static Vertex copyOf(Vertex vertex) {
        return new Vertex(new Vector4d(vertex.p));
    }

    public Vertex lerp(Vertex other, double factor, double w0, double w1) {
        Vector4d p = this.p.mul(w1, new Vector4d()).sub(other.p.mul(w0, new Vector4d())).mul(factor);
        Vertex v = new Vertex(p);
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(p, vertex.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p);
    }
}
