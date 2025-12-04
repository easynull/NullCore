package com.mw.nullcore.core.items;

import com.mw.nullcore.client.particle.screen.ScreenParticleHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GuiRenderable {
    boolean isParticleRenderable();
    @OnlyIn(Dist.CLIENT)
    default void renderLate(ScreenParticleHolder target, GuiGraphics gg, ClientLevel level, float pTick, ItemStack stack, int x, int y) {}
    @OnlyIn(Dist.CLIENT)
    default void renderEarly(ScreenParticleHolder target, GuiGraphics gg, ClientLevel level, float pTick, ItemStack stack, int x, int y) {}
}
