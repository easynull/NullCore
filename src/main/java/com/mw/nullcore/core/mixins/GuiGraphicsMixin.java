package com.mw.nullcore.core.mixins;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.items.GuiRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public final class GuiGraphicsMixin {
    @Inject(at = @At(value = "TAIL"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    private void nc$renderItem(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
        if (NullConfig.particleInGui && stack.getItem() instanceof GuiRender item) {
            GuiGraphics gg = (GuiGraphics) ((Object) this);
            item.renderItemGUI(gg, entity, level, stack, pX, pY, seed, guiOffset);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    private void nc$renderParticleItem(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
    }
}
