package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw.nullcore.core.entities.ShyItemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.NotNull;

import static com.mw.nullcore.Utils.Render.renderRays;

public final class ShyItemRender extends ItemEntityRenderer {

    public ShyItemRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(ItemEntity entity, ItemEntityRenderState state, float pTick) {
        if(state instanceof ShyItemState aState && entity instanceof ShyItemEntity aEntity){
            aState.raysEnabled = aEntity.getRays().getFirst();
            aState.color = aEntity.getRays().getSecond();
            aState.pTick = pTick;
        }
        super.extractRenderState(entity, state, pTick);
    }

    @Override
    public void render(ItemEntityRenderState state, PoseStack ps, MultiBufferSource mBuffer, int light) {
        super.render(state, ps, mBuffer, light);
        if(state instanceof ShyItemState aState && !state.item.isEmpty()) {
            if (aState.raysEnabled) {
                float yOffset = Mth.sin(state.ageInTicks / 10.0F + state.bobOffset) * 0.1F + 0.1F;
                float speed = state.item.transform().scale.y();
                ps.pushPose();
                ps.translate(0.0F, yOffset + (aState.item.usesBlockLight() ? 1f : 0.5f) * speed, 0.0F);
                renderRays(ps, mBuffer.getBuffer(RenderType.dragonRays()), aState.color, 1, aState.pTick);
                ps.popPose();
            }
        }
    }

    @Override
    public @NotNull ItemEntityRenderState createRenderState() {
        return new ShyItemState();
    }

    public static class ShyItemState extends ItemEntityRenderState {
        public boolean raysEnabled;
        public int color;
        public float pTick;
    }
}
