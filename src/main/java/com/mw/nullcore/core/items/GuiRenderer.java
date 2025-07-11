package com.mw.nullcore.core.items;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface GuiRenderer {
    void renderInGui(GuiGraphics gg, Level level, ItemStack stack, int pX, int pY, float pTick);
}
