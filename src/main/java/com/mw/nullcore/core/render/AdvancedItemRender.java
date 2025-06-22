package com.mw.nullcore.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
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

public class AdvancedItemRender extends ItemEntityRenderer {

    public AdvancedItemRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(ItemEntity entity, ItemEntityRenderState state, float p_360849_) {
        super.extractRenderState(entity, state, p_360849_);
        if(state instanceof AdvancedItemState aState && entity instanceof AdvancedItemEntity aEntity){
            aState.raysRender$Color = aEntity.raysRender$Color;
        }
    }

    @Override
    public @NotNull ItemEntityRenderState createRenderState() {
        return new AdvancedItemState();
    }

    @Override
    public void render(ItemEntityRenderState state, PoseStack ps, MultiBufferSource mBuffer, int light) {
        if(state instanceof AdvancedItemState aState && !state.item.isEmpty()) {
            if (aState.raysRender$Color.getFirst()) {
                float ticks = aState.partialTick;
                int color = aState.raysRender$Color.getSecond();
                float f1 = aState.shouldBob ? Mth.sin(aState.ageInTicks / 10.0F + aState.bobOffset) * 0.1F + 0.1F : 0;
                ps.pushPose();
                ps.translate(0, 0.25f + f1, 0);
                renderRays(ps, ticks, mBuffer.getBuffer(RenderType.dragonRays()), color);
                ps.popPose();
            }
        }
        super.render(state, ps, mBuffer, light);
    }

    private static void renderRays(PoseStack ps, float pTick, VertexConsumer buffer, int color) {
        ps.pushPose();
        final RandomSource rand = RandomSource.create(432L);
        float triangleApothem = (float)(Math.sqrt(3.0) / 2.0);
        float rotationTime = (ClientUtils.ticks + pTick) * rand.nextFloat() + 0.5f;
        float[] rgb = ColorUtils.unpackRGBA(color);
        int count = rand.nextInt(10, 15);
        Vector3f center = new Vector3f(0, 0, 0);
        for (int l = 0; l < count; l++) {
            float length = rand.nextFloat() * 1.5f;
            float width = rand.nextFloat() * 0.25f;
            Vector3f left = new Vector3f(-triangleApothem * width, length, -0.5F * width);
            Vector3f right = new Vector3f(triangleApothem * width, length, -0.5F * width);
            Vector3f front = new Vector3f(0.0F, length, width);

            Quaternionf quaternionf = new Quaternionf().rotationXYZ(rand.nextFloat() * Mth.TWO_PI + rotationTime * 0.05f, rand.nextFloat() * Mth.TWO_PI + rotationTime * 0.05f, rand.nextFloat() * Mth.TWO_PI).rotateXYZ(rand.nextFloat() * Mth.TWO_PI, rand.nextFloat() * Mth.TWO_PI, rand.nextFloat() * Mth.TWO_PI + rotationTime * 0.05f);
            ps.mulPose(quaternionf);
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
            ps.mulPose(quaternionf.invert());
        }
        ps.popPose();
    }

    public static class AdvancedItemState extends ItemEntityRenderState {
        public Pair<Boolean, Integer> raysRender$Color;
    }
}
