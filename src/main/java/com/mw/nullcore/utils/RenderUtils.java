package com.mw.nullcore.utils;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class RenderUtils {
    public static void drawTexture(ResourceLocation texture, GuiGraphics gg, int pX, int pY, int uOffset, int vOffset, int pixelWidth, int pixelHeight, int texWidth, int texHeight) {
        gg.blit(RenderType::guiTextured, texture, pX, pY, uOffset, vOffset, pixelWidth, pixelHeight, texWidth, texHeight);
    }

    public static void drawLine(PoseStack ps, float startX, float startY, float startZ, float endX, float endY, float endZ, int color, int width, boolean triD) {
        float[] argb = ColorUtils.unpackARGB(color);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (triD) RenderSystem.disableCull();
        RenderSystem.lineWidth(width);

        Matrix4f matrix = ps.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, startX, startY, startZ).setColor(argb[0], argb[1], argb[2], argb[3]);
        buffer.addVertex(matrix, endX, endY, endZ).setColor(argb[0], argb[1], argb[2], argb[3]);

        buffer.build();
        if (triD) RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void drawLine(PoseStack ps, float startX, float startY, float endX, float endY, int color, int width) {
        drawLine(ps, startX, startY, 0, endX, endY, 0, color, width, false);
    }

    public static void renderItem(GuiGraphics gg, ItemStack stack, int x, int y) {
        if(isItem3D(stack)){
            Transform tr = new Transform(gg.pose());
            tr.autoPose(()->{
                tr.scale(x, y, 0, 1f, 1f, 1f);
                gg.renderItem(stack, x, y);
            });
        } else {
            gg.renderItem(stack, x, y);
        }
    }

    public static boolean isItem3D(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() instanceof BlockItem;
    }

    public static void setColor(int rgba) {
        float[] color = ColorUtils.unpackRGBA(rgba);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
    }

    public static void autoColoring(int rgba, Runnable action) {
        float[] prevColor = RenderSystem.getShaderColor();
        setColor(rgba);
        try {
            action.run();
        } finally {
            RenderSystem.setShaderColor(prevColor[0], prevColor[1], prevColor[2], prevColor[3]);
        }
    }

    public static void drawText(Object text, GuiGraphics gg, int x, int y, int color) {
        if(text instanceof Component c) gg.drawString(mc().font, c.getString(), x, y, color, false);
        else gg.drawString(mc().font, (String) text, x, y, color, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }

    public record Transform(PoseStack ps){
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

    public static void debug(){
        float[] color = RenderSystem.getShaderColor();
        System.out.printf("Color: %.2f,%.2f,%.2f,%.2f%n", color[0], color[1], color[2], color[3]);
    }
}
