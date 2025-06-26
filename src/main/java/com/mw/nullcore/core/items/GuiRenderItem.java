package com.mw.nullcore.core.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw.nullcore.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuiRenderItem extends Item implements GuiRender, GuiParticleRender {
    final int color;
    final ParticleOptions particle;
    final String modid, path;

    public GuiRenderItem(Item.Properties properties, ParticleOptions particle, String modid, String path, int color) {
        super(properties);
        this.particle = particle;
        this.modid = modid;
        this.path = path;
        this.color = color;
    }

    public GuiRenderItem(Item.Properties properties, String modid, String path, int color) {
        this(properties, null, path, modid, color);
    }

    public GuiRenderItem(Item.Properties properties, ParticleOptions particle, int color) {
        this(properties, particle, null, null, color);
    }

    @Override
    public void renderItemGUI(GuiGraphics gg, LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset) {
        if (modid == null && path == null) return;
        PoseStack ps = gg.pose();
        ps.pushPose();
        ps.translate(pX + 8, pY + 7.5f, 100f);
        RenderUtils.RenderingBuilder.builder().renderType(RenderType::guiTextured, modid, path)
                .color(color).poseStack(ps).renderCenteredQuad(12f);
        ps.popPose();
    }

    @Override
    public void renderParticleGui(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset) {
        if (particle == null) return;
    }
}
