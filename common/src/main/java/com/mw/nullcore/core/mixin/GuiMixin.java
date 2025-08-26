package com.mw.nullcore.core.mixin;

import com.mw.nullcore.Utils;
import com.mw.nullcore.core.items.OverlayRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public final class GuiMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void nc$onRender(GuiGraphics gg, DeltaTracker ticker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        nc$checkItem(mc.player, mc.player.getMainHandItem(), gg, ticker.getGameTimeDeltaPartialTick(false), false);
        nc$checkItem(mc.player, mc.player.getOffhandItem(), gg, ticker.getGameTimeDeltaPartialTick(false), false);
        for (ItemStack stack : mc.player.getInventory().items) {
            nc$checkItem(mc.player, stack, gg, ticker.getGameTimeDeltaPartialTick(false), true);
        }
    }

    @Unique
    private void nc$checkItem(Player player, ItemStack stack, GuiGraphics gg, float pTick, boolean global) {
        Utils.Item.instanceOf(stack.getItem(), OverlayRenderer.class, item -> {
            if ((global && item.isGlobal()) || (!global && !item.isGlobal())) {
                item.renderOverlay(player, stack, gg, 0, 0, pTick);
            }
        });
    }
}
