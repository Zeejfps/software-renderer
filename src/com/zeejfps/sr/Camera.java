package com.zeejfps.sr;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class Camera {

    private Matrix4f viewMatrix;
    private Matrix4f viewProjMatrix;

    private float fov, aspect, zNear, zFar;

    public Camera(float fov, float aspect, float zNear, float zFar) {
        viewMatrix = new Matrix4f()
            .lookAt(0f, 0f, 5f,
                    0f, 0f, 0f,
                    0f, 1f, 0f);
        viewProjMatrix = new Matrix4f()
                .perspective((float) Math.toRadians(fov), aspect, zNear, zFar)
                .lookAt(0f, 0f, 10f,
                        0f, 0f, 0f,
                        0f, 1f, 0f);

    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getViewProjMatrix() {
        return viewProjMatrix;
    }

}
