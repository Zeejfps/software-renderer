package com.zeejfps.sr;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class Camera {

    public final Transform transform = new Transform();

    private final Matrix4d viewMatrix = new Matrix4d();
    private Matrix4d projMatrix;

    private float fov, aspect, zNear, zFar;

    private Quaterniond realRotation = new Quaterniond();

    public final Vector3d forward = new Vector3d(0f, 0f, -1f);
    public final Vector3d up = new Vector3d(0f, 1f, 0f);

    public Camera(double fov, double aspect, double zNear, double zFar) {
        transform.position.z = -10f;
        projMatrix = new Matrix4d()
                .perspective(Math.toRadians(fov), aspect, zNear, zFar);
    }

    public Matrix4d getViewMatrix() {
        realRotation.identity().rotateXYZ(transform.rotation.x, transform.rotation.y, transform.rotation.z);
        return viewMatrix.identity().lookAt(
                transform.position,
                forward.add(transform.position, new Vector3d()),
                up);
    }

    public Matrix4d getProjMatrix() {
        return projMatrix;
    }

}
