package com.mw.nullcore.client.render.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mw.nullcore.utils.ClientUtils;
import com.mw.nullcore.utils.ColorUtils;
import com.mw.nullcore.utils.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Function;

public final class VFXBuilder {
    private final Random rand = new Random();
    private final Vector3f[] positions = new Vector3f[]{new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0)};
    private VertexConsumer vertex;
    private PoseStack ps;
    float u0, v0, u1, v1, alpha = 1f, lifeTime, pTick = RenderUtils.partialTick;
    float[] color;
    int countPer;

    public static VFXBuilder create(PoseStack ps){
        VFXBuilder builder = new VFXBuilder();
        builder.ps = ps;
        return builder;
    }

    public static VFXBuilder copy(VFXBuilder copy){
        return copy;
    }

    public VFXBuilder renderType(Function<ResourceLocation, RenderType> type, String modId, String path) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(modId, path);
        final MultiBufferSource mBuffer = RenderUtils.mc().renderBuffers().bufferSource();
        this.vertex = mBuffer.getBuffer(type.apply(texture));
        return this;
    }

    public VFXBuilder color(float mSpeed, int... color) {
        if (mSpeed == 0) {
            this.color = ColorUtils.unpackRGBA(color[0]);
            return this;
        }
        float tick = (Mth.sin((ClientUtils.clientTick + pTick) * mSpeed) * 0.5f + 0.5f);
        float segmentDuration = 1f / (color.length - 1);
        int segment = (int)(tick / segmentDuration);
        float factor = (tick % segmentDuration) / segmentDuration;

        if (segment >= color.length - 1) {
            segment = color.length - 2;
            factor = 1f;
        }

        float[] startColor = ColorUtils.unpackRGBA(color[segment]);
        float[] endColor = ColorUtils.unpackRGBA(color[segment + 1]);

        this.color = new float[] {
                Mth.lerp(factor, startColor[0], endColor[0]),
                Mth.lerp(factor, startColor[1], endColor[1]),
                Mth.lerp(factor, startColor[2], endColor[2]),
                Mth.lerp(factor, startColor[3], endColor[3])
        };
        return this;
    }

    public VFXBuilder color(int color) {
        return color(0, color);
    }

    public VFXBuilder transparency(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public VFXBuilder poseStack(PoseStack ps) {
        this.ps = ps;
        return this;
    }

    public VFXBuilder rotate(float angel){
        ps.mulPose(new Quaternionf().rotateZ(angel));
        return this;
    }

    public VFXBuilder spinner(float mSpeed){
        float rotationTime = (ClientUtils.clientTick + pTick) * mSpeed + 0.5f;
        return rotate(rotationTime);
    }

    public VFXBuilder scale(float scale){
        ps.scale(scale, scale, 0.5f);
        return this;
    }

    public VFXBuilder pulseScale(float min, float max, float mSpeed){
        float pulse = (Mth.sin((ClientUtils.clientTick + pTick) * mSpeed) * 0.5f + 0.5f);
        float scale = min + (min - max) * pulse;
        return scale(scale);
    }

    public VFXBuilder move(float x, float y){
        return move(x, y, 100f);
    }

    public VFXBuilder move(float x, float y, float z){
        ps.translate(x, y, z);
        return this;
    }

    public VFXBuilder buildOverlay(float size){
        return addFrame(size);
    }

    private VFXBuilder lifeSettings(float lifeTime, int countPer){
        this.lifeTime = lifeTime;
        this.countPer = countPer;
        return this;
    }

    //TODO: Still in development 0_-
    private VFXBuilder buildParticle(float size, float pTick){
        return this;
    }

    private VFXBuilder addFrame(float size){
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
        this.vertex.addVertex(ps.last(), pos.x(), pos.y(), pos.z()).setColor(color[0], color[1], color[2], alpha).setUv(u, v);
    }
}
