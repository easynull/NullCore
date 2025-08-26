package com.mw.nullcore.core.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mw.nullcore.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public class GuiRenderBuilder {
    protected final MultiBufferSource mBuffer = Utils.Render.mBuffer;
    protected float u0, v0, u1, v1, a = 1f;
    protected float[] color;
    protected VertexConsumer vertex;
    protected PoseStack ps;
    protected PoseStack.Pose pose;
    protected ResourceLocation texture;

    public static GuiRenderBuilder builder() {
        return new GuiRenderBuilder();
    }

    public GuiRenderBuilder renderType(Function<ResourceLocation, RenderType> type, ResourceLocation texture) {
        this.vertex = mBuffer.getBuffer(type.apply(texture));
        this.texture = texture;
        return this;
    }

    public GuiRenderBuilder renderType(ResourceLocation texture) {
        return renderType(RenderType::guiTextured, texture);
    }

//    public GuiRenderBuilder uv(TextureAtlasSprite sprite) {
//        this.sprite = sprite;
//        this.u0 = sprite.getU0();
//        this.v0 = sprite.getV0();
//        this.u1 = sprite.getU1();
//        this.v1 = sprite.getV1();
//        return this;
//    }

    public GuiRenderBuilder pose(PoseStack ps) {
        this.ps = ps;
        pose = ps.last();
        return this;
    }

    public GuiRenderBuilder rotate(float angel){
        ps.mulPose(new Quaternionf().rotateZ(angel));
        return this;
    }

    public GuiRenderBuilder spinner(float mSpeed){
        float rotationTime = Utils.Render.getAnimationTick() * mSpeed + 0.5f;
        return rotate(rotationTime);
    }

    public GuiRenderBuilder scale(float scale){
        ps.scale(scale, scale, 0.5f);
        return this;
    }

    public GuiRenderBuilder pulseScale(float min, float max, float mSpeed){
        float pulse = (Mth.sin(Utils.Render.getAnimationTick() * mSpeed) * 0.5f + 0.5f);
        float scale = min + (min - max) * pulse;
        return scale(scale);
    }

    public GuiRenderBuilder moveBefore(float x, float y){
        ps.translate(x, y, 100f);
        return this;
    }

    public GuiRenderBuilder moveAfter(float x, float y) {
        ps.translate(x, y, 159f);
        return this;
    }

    public GuiRenderBuilder color(int color) {
        this.color = Utils.Color.unpack(color, false);
        return this;
    }

    public GuiRenderBuilder color(float r, float g, float b) {
        this.color = new float[]{r, g, b, a};
        return this;
    }

    public GuiRenderBuilder color(float speed, int... color) {
        this.color = Utils.Color.unpack(Utils.Color.getCyclingColor(speed, color), false);
        return this;
    }

    public GuiRenderBuilder alpha(float a) {
        this.a = a;
        return this;
    }

    public GuiRenderBuilder pulseAlpha(float mSpeed){
        float pulse = (Mth.sin(Utils.Render.getAnimationTick() * mSpeed) * 0.5f + 0.5f);
        return alpha(pulse);
    }

//    public GuiRenderBuilder buildParticle(float size, float lifeTime, float minCount, float maxCount, long seed){
//        return this;
//    }

    public GuiRenderBuilder buildOverlay(float size){
        return buildOverlay(size, size);
    }

    public GuiRenderBuilder buildOverlay(float width, float height){
        this.u0 = width;
        this.v0 = height;
        this.u1 = width - 1;
        this.v1 = height - 1;
        Vector3f[] positions = new Vector3f[]{new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0)};
        return renderQuad(positions, width, height);
    }

    private GuiRenderBuilder renderQuad(Vector3f[] positions, float width, float height) {
        for (Vector3f position : positions) {
            position.mul(width, height, width);
        }
        addVertex(positions[0], u0, v0);
        addVertex(positions[1], u1, v0);
        addVertex(positions[2], u1, v1);
        addVertex(positions[3], u0, v1);
        return this;
    }

    private void addVertex(Vector3f pos, float u, float v) {
        vertex.addVertex(pose, pos.x(), pos.y(), pos.z()).setColor(color[0], color[1], color[2], a).setUv(u, v);
    }
}
