package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;

public abstract class NullScreen extends Screen {
    PoseStack ps;

    protected NullScreen() {
        super(Component.empty());
    }

    protected void markData(GuiGraphics gg){
        if (ps == null) ps = gg.pose();
    }

    public PoseStack pose(){
        return ps;
    }

    public void start() {
        ps.pushPose();
    }

    public void stop() {
        ps.popPose();
    }

    public void rotate(float pX, float pY, Quaternionf angel) {
        ps.translate(pX, pY, 0);
        ps.mulPose(angel);
        ps.translate(-pX, -pY, 0);
    }

    public void scale(float pX, float pY, float sX, float sY, float sZ) {
        ps.translate(pX, pY, 0);
        ps.scale(sX, sY, sZ);
        ps.translate(-pX, -pY, 0);
    }

    public void move(float pX, float pY, float pZ) {
        ps.translate(pX, pY, pZ);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}