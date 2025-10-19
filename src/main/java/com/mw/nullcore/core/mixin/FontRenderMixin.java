package com.mw.nullcore.core.mixin;

//@Mixin(StringRenderOutput.class)
//public final class FontRenderMixin {
//    @Inject(method = "accept", at = @At("TAIL"))
//    private void nc$render(int index, Style style, int codePoint, CallbackInfoReturnable<Boolean> cir) {
//        if(style instanceof ShyStyle) {
//            StringRenderOutput output = (StringRenderOutput) ((Object) this);
//            float off = (float) Math.sin(Math.toDegrees((index * 0.02f + Utils.Render.getAnimationTick() * 0.005f * 2f)));
//            output.y += off;
//            output.color = Utils.Color.getCyclingColor(0.04f, 0xFFC8EBFF, 0xFFFFFFFF, 0xFFC00B7D);
//        }
//    }
//}
