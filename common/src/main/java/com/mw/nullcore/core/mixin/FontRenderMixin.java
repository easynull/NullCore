package com.mw.nullcore.core.mixin;

import com.mw.nullcore.Utils;
import com.mw.nullcore.client.render.ShyStyle;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.Font.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StringRenderOutput.class)
public final class FontRenderMixin {
    @Inject(method = "accept", at = @At("TAIL"))
    private void nc$render(int index, Style style, int codePoint, CallbackInfoReturnable<Boolean> cir) {
        if(style instanceof ShyStyle) {
            StringRenderOutput output = (StringRenderOutput) ((Object) this);
            float off = (float) Math.sin(Math.toDegrees((index * 0.02f + Utils.Render.getAnimationTick() * 0.005f * 2f)));
            output.y += off;
            output.color = Utils.Color.getCyclingColor(0.04f, 0xFFC8EBFF, 0xFFFFFFFF, 0xFFC00B7D);
        }
    }
}
