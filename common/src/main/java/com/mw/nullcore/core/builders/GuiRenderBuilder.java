package com.mw.nullcore.core.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mw.nullcore.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.function.Function;

public final class GuiRenderBuilder {
    static final Vector3f[] positions = new Vector3f[]{new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0)};
    static float pTick = Utils.partialTick;
    private VertexConsumer vertex;
    private PoseStack ps;
    float u0, v0, u1, v1, alpha = 1f, lifeTime;
    float[] color;
    int countPer;

    private GuiRenderBuilder(){}

    public static GuiRenderBuilder create(){
        return new GuiRenderBuilder();
    }

    public GuiRenderBuilder copyData(GuiRenderBuilder builder){
        builder.ps = this.ps;
        vertex = builder.vertex;
        u0 = builder.u0;
        u1 = builder.u1;
        v0 = builder.v0;
        v1 = builder.v1;
        alpha = builder.alpha;
        color = builder.color;
        return this;
    }

    @NotNull
    public GuiRenderBuilder renderType(Function<ResourceLocation, RenderType> type, String modId, String path) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(modId, path);
        final MultiBufferSource mBuffer = Utils.mc.renderBuffers().bufferSource();
        this.vertex = mBuffer.getBuffer(type.apply(texture));
        return this;
    }

    public GuiRenderBuilder color(float mSpeed, int... color) {
        this.color = Utils.Color.unpack(Utils.Color.lerpColors(mSpeed, color), false);
        return this;
    }

    public GuiRenderBuilder color(int color) {
        return color(0, color);
    }

    public GuiRenderBuilder alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public GuiRenderBuilder pulseAlpha(float mSpeed){
        float pulse = (Mth.sin((Utils.clientTick + pTick) * mSpeed) * 0.5f + 0.5f);
        return alpha(pulse);
    }

    public GuiRenderBuilder poseStack(PoseStack ps) {
        this.ps = ps;
        return this;
    }

    public GuiRenderBuilder rotate(float angel){
        ps.mulPose(new Quaternionf().rotateZ(angel));
        return this;
    }

    public GuiRenderBuilder spinner(float mSpeed){
        float rotationTime = (Utils.clientTick + pTick) * mSpeed + 0.5f;
        return rotate(rotationTime);
    }

    public GuiRenderBuilder scale(float scale){
        ps.scale(scale, scale, 0.5f);
        return this;
    }

    public GuiRenderBuilder pulseScale(float min, float max, float mSpeed){
        float pulse = (Mth.sin((Utils.clientTick + pTick) * mSpeed) * 0.5f + 0.5f);
        float scale = min + (min - max) * pulse;
        return scale(scale);
    }

    public GuiRenderBuilder move(float x, float y){
        return move(x, y, 100f);
    }

    public GuiRenderBuilder move(float x, float y, float z){
        ps.translate(x, y, z);
        return this;
    }

    public GuiRenderBuilder buildOverlay(float size){
        return addFrame(size);
    }

    private GuiRenderBuilder lifeSettings(float lifeTime, int countPer){
        this.lifeTime = lifeTime;
        this.countPer = countPer;
        return this;
    }

    //TODO: Still in development 0_-
    private GuiRenderBuilder buildParticle(float size, float pTick){
        return this;
    }

    private GuiRenderBuilder addFrame(float size){
        this.u0 = size;
        this.v0 = size;
        this.u1 = size - 1;
        this.v1 = size - 1;
        for(Vector3f position : positions){
            position.mul(size, size, size);
        }
        addVertex(positions[0], u0, v1);
        addVertex(positions[1], u1, v1);
        addVertex(positions[2], u1, v0);
        addVertex(positions[3], u0, v0);
        return this;
    }

    private void addVertex(Vector3f pos, float u, float v) {
        if(ps == null || vertex == null) return;
        this.vertex.addVertex(ps.last(), pos.x(), pos.y(), pos.z()).setColor(color[0], color[1], color[2], alpha).setUv(u, v);
    }
}
