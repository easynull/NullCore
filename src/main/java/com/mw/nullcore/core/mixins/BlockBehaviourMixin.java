package com.mw.nullcore.core.mixins;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.blocks.type.ContainerHave;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mw.nullcore.utils.ClientUtils.setSafeScreen;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {
    @Inject(at = @At(value = "TAIL"), method = "onRemove")
    private void nc$breaking(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof ContainerHave i)) return;
        if(state.getBlock() != newState.getBlock()){
            Containers.dropContents(level, pos, i.getInventory());
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "useWithoutItem", cancellable = true)
    private void nc$useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getBlock() instanceof ScreenHave s && s.canOpen(level, player, state)) {
            setSafeScreen(s.getScreen(level, player));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if(level.getBlockEntity(pos) instanceof MenuProvider m){
            player.openMenu(m);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
