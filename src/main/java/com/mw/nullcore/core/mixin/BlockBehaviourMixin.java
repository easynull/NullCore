package com.mw.nullcore.core.mixin;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.blocks.MenuHave;
import com.mw.nullcore.core.blocks.SaveDataBlock;
import com.mw.nullcore.core.blocks.type.ContainerHave;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.registers.NcComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
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

import static com.mw.nullcore.core.NcUtils.Client.setSafeScreen;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {
    @Inject(method = "onRemove", at = @At(value = "HEAD"))
    private void nc$onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof ContainerHave i)) return;
        if (state.getBlock() != newState.getBlock() && i.useMixinSetting()) {
            Containers.dropContents(level, pos, i.getInventory());
        }
    }

    @Inject(method = "useWithoutItem", at = @At(value = "HEAD"), cancellable = true)
    private void nc$useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.hasBlockEntity() && level.getBlockEntity(pos) instanceof ScreenHave s && s.useMixinSetting() && s.canOpen(level, player, state)) {
            setSafeScreen(s.getScreen(level, player));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (level.isClientSide() && state.getBlock() instanceof ScreenHave s && s.useMixinSetting() && s.canOpen(level, player, state)) {
            setSafeScreen(s.getScreen(level, player));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (level.getBlockEntity(pos) instanceof MenuHave m && m.useMixinSetting() && m.canOpen(level, player, state)) {
            player.openMenu(m);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "getCloneItemStack", at = @At(value = "HEAD"), cancellable = true)
    private void nc$itemClone(LevelReader level, BlockPos pos, BlockState state, boolean includeData, CallbackInfoReturnable<ItemStack> cir) {
        if (!(state.getBlock() instanceof SaveDataBlock data && state.hasBlockEntity())) return;
        BlockEntity type = level.getBlockEntity(pos);
        ItemStack stack = new ItemStack(state.getBlock().asItem());

        CompoundTag nbt = type.saveCustomOnly(level.registryAccess());
        CompoundTag additional = data.additionalData(level, pos, state, stack);
        if (additional != null && !additional.isEmpty()) {
            additional.getAllKeys().forEach(key -> nbt.put(key, additional.get(key).copy()));
        }
        stack.set(NcComponents.NBT, nbt);
        cir.setReturnValue(stack);
    }
}
