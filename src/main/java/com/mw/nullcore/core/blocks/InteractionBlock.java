package com.mw.nullcore.core.blocks;

import com.mw.nullcore.core.blocks.entities.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class InteractionBlock extends BaseEntityBlock {
    final int[] animIDs;

    public InteractionBlock(Properties prop, BlockEntityType.BlockEntitySupplier be, int... animIDs) {
        super(prop, be);
        this.animIDs = animIDs;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && animIDs != null) {
            for (int id : animIDs) {
                if (id == 0) {
                    BlockEntity tile = level.getBlockEntity(pos);
                    if (tile instanceof InventoryBlockEntity i) {
                        SimpleContainer inv = i.inventory;
                        if (!stack.isEmpty() && i.getFirst().isEmpty()) {
                            inv.setItem(0, stack.copy());
                            stack.shrink(1);
                        } else {
                            ItemStack dropped = inv.removeItem(0, 1);
                            ItemEntity item = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), dropped);
                            level.addFreshEntity(item);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        InteractionResult result = additionalUseOn(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && animIDs != null) {
            for (int id : animIDs) {
                if (id == 1) {
                    BlockEntity tile = level.getBlockEntity(pos);
                    if (tile instanceof InventoryBlockEntity i) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        InteractionResult result = additionalUseWithoutItem(state, level, pos, player, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult additionalUseOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    protected InteractionResult additionalUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }
}
