package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.builders.CameraShakeBuilder;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public final class CameraMixin {
    @Inject(method = "setup", at = @At("RETURN"))
    private void nc$onTick(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        CameraShakeBuilder.tick(self);
    }
}
