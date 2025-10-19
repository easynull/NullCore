package com.mw.nullcore.core.items;

import com.mw.nullcore.client.particle.screen.ScreenParticleHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

public interface Renderable {
    boolean isParticleRenderable();
    default void renderLate(ScreenParticleHolder target, GuiGraphics gg, ClientLevel level, float pTick, ItemStack stack, int x, int y) {}
    default void renderEarly(ScreenParticleHolder target, GuiGraphics gg, ClientLevel level, float pTick, ItemStack stack, int x, int y) {}
}
