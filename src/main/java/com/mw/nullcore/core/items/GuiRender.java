package com.mw.nullcore.core.items;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface GuiRender {
    void renderItemGUI(GuiGraphics gg, LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset);
}
