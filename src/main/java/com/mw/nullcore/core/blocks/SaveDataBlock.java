package com.mw.nullcore.core.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface SaveDataBlock {
    default CompoundTag additionalData(LevelReader level, BlockPos pos, BlockState state, Player player, ItemStack stack){
        return null;
    }

    default void additionalActions(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){}
}
