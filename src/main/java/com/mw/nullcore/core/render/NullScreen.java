package com.mw.nullcore.core.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

public abstract class NullScreen extends Screen {
    PoseStack ps;
    protected float ticks;
    final int[] animIDs;
    public final int bgWidth, bgHeight;

    protected NullScreen(int bgWidth, int bgHeight, int... animIDs) {
        super(Component.empty());
        this.bgWidth = bgWidth;
        this.bgHeight = bgHeight;
        this.animIDs = animIDs;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float pTicks) {
        markGG(gg);
        renderBackground(gg, mouseX, mouseY, pTicks);
        if (animIDs != null) {
            autoPose(() -> {
                for (int id : animIDs) {
                    switch (id) {
                        case 0 -> move(0, 200 - ticks * 200);
                        case 1 -> rotate(guiXCenter(), guiYCenter(), new Quaternionf().rotateZ(ticks * Mth.TWO_PI));
                        case 2 -> scale(guiXCenter(), guiYCenter(), ticks, ticks, ticks);
                    }
                }
                draw(gg, mouseX, mouseY, pTicks);
            });
        } else {
            draw(gg, mouseX, mouseY, pTicks);
        }
    }

    @Override
    public void tick() {
        ticks = (float) Mth.clamp(ticks + 0.03 * (1.0 + 10.0 * ticks), 0, 1);
    }

    protected abstract void draw(GuiGraphics gg, int mouseX, int mouseY, float pTicks);

    @OnlyIn(Dist.CLIENT)
    public Minecraft mc() {
        return Minecraft.getInstance();
    }

    public void markGG(GuiGraphics gg) {
        if (gg != null) ps = gg.pose();
    }

    public PoseStack ps() {
        return ps;
    }

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

    public void rotate(float pX, float pY, Quaternionf angel) {
        move(pX, pY);
        ps.mulPose(angel);
        move(-pX, -pY);
    }

    public void scale(float pX, float pY, float sX, float sY, float sZ) {
        move(pX, pY);
        ps.scale(sX, sY, sZ);
        move(-pX, -pY);
    }

    public void scale(float pX, float pY, float sX, float sY) {
        scale(pX, pY, sX, sY, 0);
    }

    public void move(float pX, float pY, float pZ) {
        ps.translate(pX, pY, pZ);
    }

    public void move(float pX, float pY) {
        move(pX, pY, 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public int guiLeft() {
        return (width - bgWidth) / 2;
    }

    public int guiTop() {
        return (height - bgHeight) / 2;
    }

    public int guiXCenter() {
        return width / 2;
    }

    public int guiYCenter() {
        return height/ 2;
    }
}