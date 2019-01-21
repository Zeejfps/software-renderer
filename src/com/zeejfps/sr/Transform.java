package com.zeejfps.sr;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f scale = new Vector3f(1f, 1f, 1f);

    private final Quaternionf realRotation = new Quaternionf();

    private final Matrix4f transformationMatrix = new Matrix4f();

    public Matrix4f getTransformationMatrix() {
        realRotation.identity()
                .rotateXYZ(rotation.x, rotation.y, rotation.z);
        transformationMatrix.identity()
                .translate(position)
                .rotate(realRotation)
                .scale(scale);
        return transformationMatrix;
    }

}
