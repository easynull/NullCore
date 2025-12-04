package com.mw.nullcore.core.blocks;

import com.mw.nullcore.registers.NcComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public interface SaveDataBlock {
    default CompoundTag additionalData(ItemStack stack, LevelReader level, BlockPos pos, BlockState state){
        return null;
    }

    default void additionalActions(ItemStack stack, Level level, BlockPos pos, BlockState state, LivingEntity placer){}

    default boolean removeForStack(ItemStack stack, LevelReader level, BlockPos pos, BlockState state){
        return false;
    }

    default Supplier<DataComponentType<CompoundTag>> getCustomComponent(){
        return NcComponents.NBT;
    }
}
