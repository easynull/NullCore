package com.mw.nullcore.core.items;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface OverlayRenderer {
    @OnlyIn(Dist.CLIENT)
    void renderOverlay(Player player, ItemStack stack, GuiGraphics gg, int pX, int pY, float pTick);
    default boolean isGlobal(){
        return false;
    }
}
