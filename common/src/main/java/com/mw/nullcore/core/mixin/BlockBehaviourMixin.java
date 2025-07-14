package com.mw.nullcore.core.mixin;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.blocks.SaveDataBlock;
import com.mw.nullcore.core.blocks.type.ContainerHave;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static com.mw.nullcore.Utils.Client.setSafeScreen;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {
    @Inject(at = @At(value = "TAIL"), method = "onRemove")
    private void nc$breaking(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof ContainerHave i)) return;
        if (state.getBlock() != newState.getBlock()) {
            Containers.dropContents(level, pos, i.getInventory());
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "useWithoutItem", cancellable = true)
    private void nc$useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getBlock() instanceof ScreenHave s && s.canOpen(level, player, state)) {
            setSafeScreen(s.getScreen(level, player));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (level.getBlockEntity(pos) instanceof MenuProvider m) {
            player.openMenu(m);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getCloneItemStack", cancellable = true)
    private void nc$cloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, CallbackInfoReturnable<ItemStack> cir) {
        if (!(state.getBlock() instanceof SaveDataBlock data && state.hasBlockEntity())) return;
        BlockEntity type = level.getBlockEntity(pos);
        ItemStack stack = new ItemStack(state.getBlock().asItem());

        CompoundTag nbt = type.saveCustomOnly(level.registryAccess());
        CompoundTag additional = data.additionalData(level, pos, state, stack);
        if (additional != null && !additional.isEmpty()) {
            additional.getAllKeys().forEach(key -> nbt.put(key, additional.get(key).copy()));
        }
        stack.set(NullComponents.nbt.get(), nbt);
        cir.setReturnValue(stack);
    }
}
