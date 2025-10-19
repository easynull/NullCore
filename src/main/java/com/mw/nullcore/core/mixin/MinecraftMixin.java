package com.mw.nullcore.core.mixin;

import com.mw.nullcore.client.audio.ClientTracker;
import com.mw.nullcore.client.particle.screen.ScreenParticleHandler;
import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public final class MinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void nc$onTick(CallbackInfo ci) {
        if (!NcUtils.mc.isPaused()) {
            ScreenParticleHandler.tick();
            ClientTracker.tick();
            NcUtils.clientTick++;
        }
    }

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void nc$getMusic(CallbackInfoReturnable<MusicInfo> cir) {
        if (Minecraft.getInstance().player != null && TracksManager.selfTracks.containsKey(Minecraft.getInstance().player.level().dimension().location())) {
            cir.setReturnValue(null);
        }
    }
}
