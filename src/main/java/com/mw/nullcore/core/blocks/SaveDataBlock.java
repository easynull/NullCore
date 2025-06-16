package com.mw.nullcore.core.blocks;

import com.mw.nullcore.core.components.NullComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class SaveDataBlock extends InteractionBlock {

    public SaveDataBlock(Properties prop, BlockEntityType.BlockEntitySupplier be) {
        super(prop, be);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData, player);
        if(level.getBlockEntity(pos) instanceof BlockEntity be) {
            CompoundTag nbt = be.saveCustomOnly(level.registryAccess());
            CompoundTag additional = additionalData(level, pos, state, player, stack);
            if (additional != null){
                for (String key : additional.getAllKeys()) {
                    nbt.put(key, additional.get(key).copy());
                }
            }
            stack.set(NullComponents.nbt, nbt);
        }
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if(level.getBlockEntity(pos) instanceof BlockEntity be && stack.has(NullComponents.nbt)) {
            be.loadCustomOnly(stack.get(NullComponents.nbt), level.registryAccess());
            additionalActions(level, pos, state, placer, stack);
        }
    }

    protected CompoundTag additionalData(LevelReader level, BlockPos pos, BlockState state, Player player, ItemStack stack){
        return null;
    }

    protected void additionalActions(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){}
}
