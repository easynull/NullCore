package com.mw.nullcore.core.mixins;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.blocks.SaveDataBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mw.nullcore.utils.ClientUtils.setSafeScreen;

@Mixin(Block.class)
public final class BlockMixin {
    @Inject(at = @At(value = "TAIL"), method = "setPlacedBy")
    private void nc$setPlaced(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (!(state.getBlock() instanceof SaveDataBlock b && state.hasBlockEntity() && stack.has(NullComponents.nbt))) return;
        level.getBlockEntity(pos).loadCustomOnly(stack.get(NullComponents.nbt), level.registryAccess());
        b.additionalActions(level, pos, state, placer, stack);
    }
}
