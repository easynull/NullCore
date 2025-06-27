package com.mw.nullcore.core.mixins;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.items.GuiRenderer;
import com.mw.nullcore.utils.RenderUtils;
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
    private void nc$renderItemBefore(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
        if (NullConfig.VFXGui && stack.getItem() instanceof GuiRenderer item) {
            GuiGraphics gg = (GuiGraphics) ((Object) this);
            item.renderGuiVFX(gg, level, stack, pX, pY, RenderUtils.partialTick);
        }
    }
}
