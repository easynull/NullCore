package com.mw.nullcore.core.mixin;

import com.mw.nullcore.Utils;
import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.items.GuiRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public final class GuiGraphicsMixin {
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "TAIL"))
    private void nc$renderItem(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset, CallbackInfo ci) {
        GuiGraphics gg = (GuiGraphics) ((Object) this);
        if(!NullConfig.enableGuiVFX.get()) return;
        if (stack.getItem() instanceof GuiRenderer item) {
            item.renderInGui(gg, level, stack, pX, pY, Utils.partialTick);
        } else if(stack.getItem() instanceof BlockItem bi && bi instanceof GuiRenderer item){
            item.renderInGui(gg, level, stack, pX, pY, Utils.partialTick);
        }
    }
}
