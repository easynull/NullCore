package com.mw.nullcore.core.items;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface OverlayRenderer {
    void renderOverlay(Player player, ItemStack stack, GuiGraphics gg, int pX, int pY, float pTick);
    default boolean isGlobal(){
        return false;
    }
}
