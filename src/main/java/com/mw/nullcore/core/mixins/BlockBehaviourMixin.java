package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.blocks.type.ContainerHave;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {
    @Inject(at = @At(value = "TAIL"), method = "onRemove")
    private void nc$breaking(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof ContainerHave i)) return;
        if(state.getBlock() != newState.getBlock()){
            Containers.dropContents(level, pos, i.getInventory());
        }
    }
}
