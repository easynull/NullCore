package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.items.OverlayRenderer;
import com.mw.nullcore.client.particle.screen.ScreenParticleHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public final class GuiMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void nc$onRender(GuiGraphics gg, DeltaTracker ticker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        ScreenParticleHandler.render();
        nc$checkElement(mc.player, mc.player.getMainHandItem(), gg, ticker.getGameTimeDeltaPartialTick(false), false);
        nc$checkElement(mc.player, mc.player.getOffhandItem(), gg, ticker.getGameTimeDeltaPartialTick(false), false);
        for (ItemStack stack : mc.player.getInventory().items) {
            nc$checkElement(mc.player, stack, gg, ticker.getGameTimeDeltaPartialTick(false), true);
        }
    }

    @Unique
    private void nc$checkElement(Player player, ItemStack stack, GuiGraphics gg, float pTick, boolean global) {
        BlockHitResult hit = (BlockHitResult) player.pick(5.0f, 0.0f, false);
        if (player.level().getBlockState(hit.getBlockPos()).getBlock() instanceof OverlayRenderer o) {
            o.renderOverlay(player, new ItemStack(player.level().getBlockState(hit.getBlockPos()).getBlock()), gg, 0, 0, pTick);
            return;
        }
        NcUtils.Item.instanceOf(stack.getItem(), OverlayRenderer.class, item -> {
            if ((global && item.isGlobal()) || (!global && !item.isGlobal())) {
                item.renderOverlay(player, stack, gg, 0, 0, pTick);
            }
        });
    }
}
