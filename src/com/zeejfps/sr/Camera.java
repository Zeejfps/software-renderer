package com.zeejfps.sr;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {

    public final Transform transform = new Transform();

    private final Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projMatrix;

    private float fov, aspect, zNear, zFar;

    private Quaternionf realRotation = new Quaternionf();

    public final Vector3f forward = new Vector3f(0f, 0f, -1f);
    private Vector3f up = new Vector3f(0f, 1f, 0f);
    public final Vector3f right = forward.cross(up, new Vector3f());

    public Camera(float fov, float aspect, float zNear, float zFar) {
        transform.position.z = 10f;
        projMatrix = new Matrix4f()
                .perspective((float) Math.toRadians(fov), aspect, zNear, zFar);
    }

    public Matrix4f getViewMatrix() {
        realRotation.identity().rotateXYZ(transform.rotation.x, transform.rotation.y, transform.rotation.z);
        return viewMatrix.identity().lookAt(
                transform.position,
                forward.add(transform.position, new Vector3f()),
                up);
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

}
