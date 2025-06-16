package com.mw.nullcore.core.items;

import com.mw.nullcore.core.multiblocks.Blueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface BlueprintActivator {

    default void activateMB(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!level.isClientSide && player != null) {
            for (Blueprint bb : Blueprint.registry.values()) {
                if (bb.canActivate(player, level, stack)) {
                    Blueprint.Structure structure = bb.getStructure(level, pos);
                    bb.onBuilt(level, pos, structure, null);
                }
            }
        }
    }
}
