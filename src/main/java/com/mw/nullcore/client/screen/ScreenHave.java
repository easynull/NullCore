package com.mw.nullcore.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ScreenHave {
    Screen getScreen(Level level, Player player, BlockPos pos);

    default boolean canOpen(Level level, Player player, ItemStack stack){
        return true;
    }
}
