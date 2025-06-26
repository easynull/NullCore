package com.mw.nullcore.core.blocks;

import com.mw.nullcore.core.blocks.type.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public interface InteractionBlock {
    default InteractionResult actionInventory(BlockState state, ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (!(level.getBlockEntity(pos) instanceof InventoryBlockEntity i)) return InteractionResult.CONSUME;
        if (!level.isClientSide) {
            SimpleContainer inv = i.inventory;
            System.out.print(inv.getItems().size());
            if(i.getFirst().isEmpty()){
                inv.setItem(0, stack);
                if(!player.isCreative()){
                    player.getItemInHand(hand).shrink(1);
                }
                i.setChanged();
                return InteractionResult.SUCCESS;
            }

            if(!i.getFirst().isEmpty()){
                ItemHandlerHelper.giveItemToPlayer(player, i.getFirst());
                i.inventory.removeItem(0, 1);
                i.setChanged();
                return InteractionResult.SUCCESS;
            }
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
        return InteractionResult.CONSUME;
    }

    default InteractionResult actionMenu(Level level, BlockPos pos, Player player){
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider m) {
            player.openMenu(m);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
