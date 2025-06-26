package com.mw.nullcore.core.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface GuiParticleRender {
    void renderParticleGui(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset);
}
