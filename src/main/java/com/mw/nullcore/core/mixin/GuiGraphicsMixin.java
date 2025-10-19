package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.items.Renderable;
import com.mw.nullcore.client.particle.screen.ScreenParticleHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public final class GuiGraphicsMixin {
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "HEAD"))
    private void nc$renderItemEarly(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
        GuiGraphics gg = (GuiGraphics) ((Object) this);
        if (!NcConfig.enableGuiVFX.get()) return;
        NcUtils.Item.instanceOf(stack.getItem(), Renderable.class, item -> {
            if (item.isParticleRenderable()) ScreenParticleHandler.renderItemStackEarly(gg.pose(), stack, pX, pY);
            else item.renderEarly(null, gg, (ClientLevel) level, NcUtils.partialTick, stack, pX, pY);
        });
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "TAIL"))
    private void nc$renderItemLate(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
        GuiGraphics gg = (GuiGraphics) ((Object) this);
        if (!NcConfig.enableGuiVFX.get()) return;
        NcUtils.Item.instanceOf(stack.getItem(), Renderable.class, item -> {
            if (item.isParticleRenderable()) ScreenParticleHandler.renderItemStackLate();
            else item.renderLate(null, gg, (ClientLevel) level, NcUtils.partialTick, stack, pX, pY);
        });
    }
}
