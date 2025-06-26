package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.blocks.SaveDataBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockExtension.class)
public interface BlockExtensionMixin {
    @Inject(at = @At(value = "TAIL"), method = "getCloneItemStack", cancellable = true)
    private void nc$getItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (!(state.getBlock() instanceof SaveDataBlock b && state.hasBlockEntity())) return;
        ItemStack stack = cir.getReturnValue();
        CompoundTag nbt = level.getBlockEntity(pos).saveCustomOnly(level.registryAccess());
        CompoundTag additional = b.additionalData(level, pos, state, player, stack);
        if (additional != null && !additional.isEmpty()) {
            for (String key : additional.getAllKeys()) {
                nbt.put(key, additional.get(key).copy());
            }
        }
        stack.set(NullComponents.nbt, nbt);
        cir.setReturnValue(stack);
    }
}
