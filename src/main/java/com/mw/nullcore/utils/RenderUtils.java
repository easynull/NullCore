package com.mw.nullcore.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Function;

public final class RenderUtils {
    private static final MultiBufferSource mBuffer = mc().renderBuffers().bufferSource();

    public static void drawTexture(ResourceLocation texture, GuiGraphics gg, int pX, int pY, int uOffset, int vOffset, int pixelWidth, int pixelHeight, int texWidth, int texHeight) {
        gg.blit(RenderType::guiTextured, texture, pX, pY, uOffset, vOffset, pixelWidth, pixelHeight, texWidth, texHeight);
    }

    public static void drawLine(PoseStack ps, float startX, float startY, float startZ, float endX, float endY, float endZ, int color, int width, boolean triD) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (triD) RenderSystem.disableCull();
        RenderSystem.lineWidth(width);

        PoseStack.Pose pose = ps.last();
        VertexConsumer buffer = mBuffer.getBuffer(RenderType.debugLineStrip(width));

        buffer.addVertex(pose, startX, startY, startZ).setColor(color);
        buffer.addVertex(pose, endX, endY, endZ).setColor(color);
        if (triD) RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void drawLine(PoseStack ps, float startX, float startY, float endX, float endY, int color, int width) {
        drawLine(ps, startX, startY, 0, endX, endY, 0, color, width, false);
    }

    public static void renderRays(PoseStack ps, float pTick, VertexConsumer buffer, int color) {
        ps.pushPose();
        RandomSource rand = RandomSource.create(1L);
        float triangleApothem = (float) (Math.sqrt(3.0) / 2.0);
        float rotationTime = (ClientUtils.clientTick + pTick) * rand.nextFloat() + 0.5f;
        float[] rgb = ColorUtils.unpackRGBA(color);
        int count = rand.nextInt(10, 25);
        final Vector3f center = new Vector3f(0, 0, 0);
        for (int l = 0; l < count; l++) {
            float length = rand.nextFloat();
            float width = rand.nextFloat() * 0.3f;
            Vector3f left = new Vector3f(-triangleApothem * width, length, -0.5F * width);
            Vector3f right = new Vector3f(triangleApothem * width, length, -0.5F * width);
            Vector3f front = new Vector3f(0.0F, length, width);

            Quaternionf rotate = new Quaternionf().rotationXYZ(rand.nextFloat() * Mth.TWO_PI + rotationTime * 0.05f, Mth.TWO_PI + rotationTime * 0.08f, rand.nextFloat() * Mth.TWO_PI).rotateXYZ(rand.nextFloat() * Mth.TWO_PI, rand.nextFloat() * Mth.TWO_PI, rand.nextFloat() * Mth.TWO_PI + rotationTime * 0.06f);
            ps.mulPose(rotate);
            PoseStack.Pose pose = ps.last();
            buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
            buffer.addVertex(pose, left).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
            buffer.addVertex(pose, right).setColor(rgb[0], rgb[1], rgb[2], 0.0f);

            buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
            buffer.addVertex(pose, right).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
            buffer.addVertex(pose, front).setColor(rgb[0], rgb[1], rgb[2], 0.0f);

            buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
            buffer.addVertex(pose, front).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
            buffer.addVertex(pose, left).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
            ps.mulPose(rotate.invert());
        }
        ps.popPose();
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

    public static void drawText(Object text, GuiGraphics gg, int pX, int pY, int color, boolean shadow) {
        if (text instanceof Component c) gg.drawString(mc().font, c.getString(), pX, pY, color, shadow);
        else gg.drawString(mc().font, (String) text, pX, pY, color, shadow);
    }

    public static void drawText(Object text, GuiGraphics gg, int pX, int pY, int color) {
        drawText(text, gg, pX, pY, color, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }

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

    public static final class RenderingBuilder {
        MultiBufferSource mBuffer = RenderUtils.mc().renderBuffers().bufferSource();
        VertexConsumer vertex;
        PoseStack ps;
        PoseStack.Pose pose;
        ResourceLocation loc;
        float u0, v0, u1, v1;
        float[] color;

        public static RenderingBuilder start() {
            return new RenderingBuilder();
        }

        public RenderingBuilder renderType(Function<ResourceLocation, RenderType> type, ResourceLocation loc) {
            this.loc = loc;
            this.vertex = mBuffer.getBuffer(type.apply(loc));
            return this;
        }

        public RenderingBuilder poseStack(PoseStack ps) {
            this.ps = ps;
            pose = ps.last();
            return this;
        }

        public RenderingBuilder color(int color) {
            this.color = ColorUtils.unpackRGBA(color);
            return this;
        }

        public RenderingBuilder renderCenteredQuad(float size){
            return renderCenteredQuad(size, size);
        }

        public RenderingBuilder renderCenteredQuad(float width, float height){
            Vector3f[] positions = new Vector3f[]{new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0)};
            return renderQuad(positions, width, height);
        }

        public RenderingBuilder renderQuad(Vector3f[] positions, float size){
            return renderQuad(positions, size, size);
        }

        public RenderingBuilder renderQuad(Vector3f[] positions, float width, float height){
            this.u0 = width;
            this.v0 = height;
            this.u1 = width - 1;
            this.v1 = height - 1;
            for(Vector3f position : positions){
                position.mul(width, height, width);
            }
            return renderQuad(positions);
        }

        private RenderingBuilder renderQuad(Vector3f[] positions){
            addVertex(positions[0], u0, v1);
            addVertex(positions[1], u1, v1);
            addVertex(positions[2], u1, v0);
            addVertex(positions[3], u0, v0);
            return this;
        }

        private void addVertex(Vector3f pos, float u, float v) {
            vertex.addVertex(pose, pos.x(), pos.y(), pos.z()).setColor(color[3], color[1], color[2], 1f).setUv(u, v);
        }
    }
}
