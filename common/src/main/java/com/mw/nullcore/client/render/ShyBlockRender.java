package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mw.nullcore.Utils;
import com.mw.nullcore.core.entities.ShyBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public final class ShyBlockRender extends EntityRenderer<ShyBlockEntity, ShyBlockRender.ShyBlockState> {
    final EntityRendererProvider.Context context;
    public ShyBlockRender(EntityRendererProvider.Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void render(ShyBlockState state, PoseStack ps, MultiBufferSource buffer, int light) {
        final Random rand = new Random(state.blockPos.asLong());
        Transform.create(ps, tr -> tr.autoPose(() -> {
            tr.move(-0.5f, 0.2f, -0.5f);
            if (state.anim == ShyBlockEntity.Type.mixing) {
                ps.mulPose(Axis.XP.rotation((rand.nextFloat() - 0.5f) * 0.2f));
                ps.mulPose(Axis.YP.rotation((rand.nextFloat() - 0.5f) * 0.2f));
                ps.mulPose(Axis.ZP.rotation((rand.nextFloat() - 0.5f) * 0.2f));
            } else if (state.anim == ShyBlockEntity.Type.rebound) {
                ps.translate(0, Mth.clamp(Mth.sin(state.lifeTime * 0.2f + Mth.HALF_PI) * 1.4f + rand.nextFloat(), -0.3f, 3f), 0);
            } else if (state.anim == ShyBlockEntity.Type.fall){
                ps.translate(0, 5, 0);
            }
            if (state.lifeTime != 999 || state.anim == ShyBlockEntity.Type.fall && state.lifeTime <= Utils.Mth.secondTick(3)) {
                float scale = Mth.clamp(state.lifeTime * 0.08f, 0, 1f);
                tr.scale(0.5f, state.anim.yOffset, 0.5f, scale, scale, scale);
            }
            context.getBlockRenderDispatcher().renderSingleBlock(state.blockState, ps, buffer, light, OverlayTexture.NO_OVERLAY);
        }));
    }

    @Override
    public void extractRenderState(ShyBlockEntity entity, ShyBlockState state, float pTick) {
        state.blockState = entity.getBlock();
        state.blockPos = entity.getOnPos();
        state.lifeTime = entity.getLifeTime();
        state.anim = entity.getAnimType();
    }

    @Override
    public ShyBlockState createRenderState() {
        return new ShyBlockState();
    }

    @Override
    public boolean shouldRender(ShyBlockEntity entity, Frustum camera, double camX, double camY, double camZ) {
        AABB aabb = this.getBoundingBoxForCulling(entity).inflate(1.6F);
        return camera.isVisible(aabb);
    }

    public static class ShyBlockState extends FallingBlockRenderState {
        public int lifeTime;
        public ShyBlockEntity.Type anim;
    }
}
