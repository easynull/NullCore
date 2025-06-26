package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.blocks.type.InventoryBlockEntity;
import com.mw.nullcore.core.blocks.InteractionBlock;
import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {
    @Inject(at = @At(value = "TAIL"), method = "useItemOn", cancellable = true)
    private void nc$useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(state.getBlock() instanceof InteractionBlock b && level.getBlockEntity(pos) instanceof InventoryBlockEntity i)) return;
        SimpleContainer inv = i.inventory;
        if (!i.getFirst().isEmpty()) return;
        ItemStack stackToAdd = stack.copyWithCount(1);
        inv.setItem(0, stackToAdd);
        stack.shrink(1);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE,pos);
        ClientUtils.sendPacketDispatch(i);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(at = @At(value = "TAIL"), method = "useWithoutItem", cancellable = true)
    private void nc$useWithoutItemOn(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(state.getBlock() instanceof InteractionBlock b && level.getBlockEntity(pos) instanceof InventoryBlockEntity i)) return;
        SimpleContainer inv = i.inventory;
        if (i.getFirst().isEmpty()) return;
        ItemHandlerHelper.giveItemToPlayer(player, i.getFirst());
        inv.removeItem(0, 1);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE,pos);
        ClientUtils.sendPacketDispatch(i);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(at = @At(value = "TAIL"), method = "onRemove")
    private void nc$breaking(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!(state.getBlock() instanceof InteractionBlock && level.getBlockEntity(pos) instanceof InventoryBlockEntity i)) return;
        if(state.getBlock() != newState.getBlock()){
            Containers.dropContents(level, pos, i.inventory);
        }
    }
}
