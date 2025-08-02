package com.mw.nullcore.core.blocks;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface MenuHave extends MenuProvider {
    default boolean canOpen(Level level, Player player, Object it){
        return true;
    }
    default boolean useMixinSetting(){
        return true;
    }
}
