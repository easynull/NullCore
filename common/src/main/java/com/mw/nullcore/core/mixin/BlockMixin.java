package com.mw.nullcore.core.mixin;

import com.mw.nullcore.registers.NullComponents;
import com.mw.nullcore.core.blocks.SaveDataBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public final class BlockMixin {
    @Inject(method = "setPlacedBy", at = @At(value = "TAIL"))
    private void nc$setPlaced(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (!(state.getBlock() instanceof SaveDataBlock b && state.hasBlockEntity() && stack.has(NullComponents.nbt.get()))) return;
        level.getBlockEntity(pos).loadCustomOnly(stack.get(NullComponents.nbt.get()), level.registryAccess());
        b.additionalActions(level, pos, state, placer, stack);
    }
}
