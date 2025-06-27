package com.mw.nullcore.core.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw.nullcore.client.render.vfx.VFXBuilder;
import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class GuiRenderItem extends Item implements GuiRenderer {
    private final ResourceLocation texture;
    final int color;
    final float size;
    final boolean late;

    public GuiRenderItem(Item.Properties properties, ResourceLocation texture, int color, float size, boolean late) {
        super(properties);
        this.texture = texture;
        this.color = color;
        this.size = size;
        this.late = late;
    }

    public GuiRenderItem(Item.Properties properties, ResourceLocation texture, int color, float size) {
        this(properties, texture, color, size, false);
    }

    @Override
    public void renderGuiVFX(GuiGraphics gg, Level level, ItemStack stack, int pX, int pY, float pTick) {
        PoseStack ps = gg.pose();
        ps.pushPose();
        VFXBuilder.create(ps).move(pX + 8.5f, pY + 8.5f, late ? 200f : 100f).renderType(RenderType::guiTextured, texture.getNamespace(), texture.getPath()).color(color).build(size);
        ps.popPose();
    }
}
