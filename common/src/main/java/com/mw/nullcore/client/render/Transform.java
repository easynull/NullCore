package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;

public record Transform(PoseStack ps) {
    public void start() {
        ps.pushPose();
    }

    public void stop() {
        ps.popPose();
    }

    public void autoPose(Runnable action) {
        start();
        action.run();
        stop();
    }

    public void rotate(float pX, float pY, float pZ, Quaternionf angel) {
        move(pX, pY, pZ);
        ps.mulPose(angel);
        move(-pX, -pY, -pZ);
    }

    public void rotate(float pX, float pY, Quaternionf angel) {
        rotate(pX, pY, 0, angel);
    }

    public void scale(float pX, float pY, float pZ, float sX, float sY, float sZ) {
        move(pX, pY, pZ);
        ps.scale(sX, sY, sZ);
        move(-pX, -pY, -pZ);
    }

    public void scale(float pX, float pY, float sX, float sY) {
        scale(pX, pY, 0, sX, sY, 0);
    }

    public void move(float pX, float pY, float pZ) {
        ps.translate(pX, pY, pZ);
    }

    public void move(float pX, float pY) {
        move(pX, pY, 0);
    }
}
