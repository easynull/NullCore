package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mw.nullcore.core.entities.AdvancedItemEntity;
import com.mw.nullcore.utils.ClientUtils;
import com.mw.nullcore.utils.ColorUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.mw.nullcore.utils.RenderUtils.renderRays;

@OnlyIn(Dist.CLIENT)
public final class AdvancedItemRender extends ItemEntityRenderer {

    public AdvancedItemRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(ItemEntity entity, ItemEntityRenderState state, float pTick) {
        if(state instanceof AdvancedItemState aState && entity instanceof AdvancedItemEntity aEntity){
            aState.raysEnabled = aEntity.getRays().getFirst();
            aState.color = aEntity.getRays().getSecond();
        }
        super.extractRenderState(entity, state, pTick);
    }

    @Override
    public void render(ItemEntityRenderState state, PoseStack ps, MultiBufferSource mBuffer, int light) {
        super.render(state, ps, mBuffer, light);
        if(state instanceof AdvancedItemState aState && !state.item.isEmpty()) {
            if (aState.raysEnabled) {
                float yOffset = state.shouldBob ? Mth.sin(state.ageInTicks / 10.0F + state.bobOffset) * 0.1F + 0.1F : 0;
                float speed = state.item.transform().scale.y();
                ps.pushPose();
                ps.translate(0.0F, yOffset + 0.5F * speed, 0.0F);
                renderRays(ps, aState.partialTick, mBuffer.getBuffer(RenderType.dragonRays()), aState.color);
                ps.popPose();
            }
        }
    }

    @Override
    public @NotNull ItemEntityRenderState createRenderState() {
        return new AdvancedItemState();
    }

    @OnlyIn(Dist.CLIENT)
    public static class AdvancedItemState extends ItemEntityRenderState {
        public boolean raysEnabled;
        public int color;
    }
}
