package com.mw.nullcore.client.render.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mw.nullcore.utils.ColorUtils;
import com.mw.nullcore.utils.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public final class VFXBuilder {
    private final MultiBufferSource mBuffer = RenderUtils.mc().renderBuffers().bufferSource();
    private ResourceLocation texture;
    private VertexConsumer vertex;
    private PoseStack ps;
    float u0, v0, u1, v1, alpha = 1f;
    float[] color;

    public static VFXBuilder create(PoseStack ps){
        VFXBuilder builder = new VFXBuilder();
        builder.ps = ps;
        return builder;
    }

    public static VFXBuilder copy(VFXBuilder copy){
        return copy;
    }

    public VFXBuilder renderType(Function<ResourceLocation, RenderType> type, String modId, String path) {
        this.texture = ResourceLocation.fromNamespaceAndPath(modId, path);
        this.vertex = mBuffer.getBuffer(type.apply(texture));
        return this;
    }

    public VFXBuilder color(int color) {
        this.color = ColorUtils.unpackRGBA(color);
        return this;
    }

    public VFXBuilder transparency(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public VFXBuilder poseStack(PoseStack ps) {
        this.ps = ps;
        return this;
    }

    public VFXBuilder spin(float angel){
        ps.mulPose(new Quaternionf().rotateZ(angel));
        return this;
    }

    public VFXBuilder scale(float scale){
        ps.scale(scale, scale, 0.5f);
        return this;
    }

    public VFXBuilder move(float x, float y){
        return move(x, y, 100f);
    }

    public VFXBuilder move(float x, float y, float z){
        ps.translate(x, y, z);
        return this;
    }

    public VFXBuilder build(float size){
        this.u0 = size;
        this.v0 = size;
        this.u1 = size - 1;
        this.v1 = size - 1;
        Vector3f[] positions = new Vector3f[]{new Vector3f(-1, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0)};
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
