package com.zeejfps.sr;

import org.joml.*;

public class Transform {

    public final Vector3d position = new Vector3d();
    public final Vector3d rotation = new Vector3d();
    public final Vector3d scale = new Vector3d(1, 1, 1);

    private final Quaterniond realRotation = new Quaterniond();

    private final Matrix4d transformationMatrix = new Matrix4d();

    public Matrix4d getModelMatrix() {
        realRotation.identity()
                .rotateXYZ(rotation.x, rotation.y, rotation.z);
        transformationMatrix.identity()
                .translate(position)
                .rotate(realRotation)
                .scale(scale);
        return transformationMatrix;
    }

}
