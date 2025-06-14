package com.mw.nullcore.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public abstract class NullScreen extends Screen {
    PoseStack ps;
    protected float ticks;
    final int[] animIDs;

    protected NullScreen(int... animIDs) {
        super(Component.empty());
        this.animIDs = animIDs;
    }

    protected NullScreen() {
        this((int[]) null);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float pTicks) {
        renderBackground(gg, mouseX, mouseY, pTicks);
        start();
        if(animIDs != null) {
            ticks = (float) Mth.clamp(Math.pow(ticks + 0.4, 2.2d), 0, 1);
            for (int id : animIDs) {
                switch (id) {
                    case 0 -> move(0, 200 - ticks * 200, 0); //up drawing
                    case 1 -> scale(width / 2f, height / 2f, ticks, ticks, 0); // approximation drawing
                }
            }
        }
        draw(gg, mouseX, mouseY, pTicks);
        stop();
    }

    protected abstract void draw(GuiGraphics gg, int mouseX, int mouseY, float pTicks);

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