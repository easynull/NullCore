package com.mw.nullcore.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ScreenHave {
    Screen getScreen(Level level, Player player);

    default boolean canOpen(Level level, Player player, Object it){
        return true;
    }
    default boolean useMixinSetting(){
        return true;
    }
}
