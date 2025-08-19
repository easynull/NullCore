package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.managers.LockableManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public final class GameModeMixin {
    @Shadow
    private ServerLevel level;

    @Shadow @Final
    private ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void nc$onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (LockableManager.canBlocked(this.player, this.player.getUsedItemHand(), pos, this.level)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void nc$onPlacedBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        BlockPos pos = hitResult.getBlockPos();
        if (LockableManager.canBlocked(player, hand, pos, this.level) && stack.getItem() instanceof BlockItem) {
            player.connection.send(new ClientboundBlockUpdatePacket(pos, level.getBlockState(pos)));
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
