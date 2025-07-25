package com.mw.nullcore.core.mixin;

import com.google.common.collect.Multimap;
import com.mw.nullcore.client.audio.Fading;
import com.mw.nullcore.client.audio.TrackerController;
import com.mw.nullcore.client.audio.TrackerTicker;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements Fading {
    @Shadow(remap = false)
    public abstract void setVolume(SoundInstance sound, float volume);

    @Shadow(remap = false)
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow
    public abstract void stop(SoundInstance sound);

    @Unique
    private boolean nc$fading;
    @Unique
    private int nc$tick;
    @Unique
    private SoundInstance nc$sound;

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void nc$onTick(CallbackInfo ci) {
        if (nc$fading) {
            if (nc$sound != null) {
                if (nc$tick > 0) {
                    --nc$tick;
                    setVolume(nc$sound, Math.max(0, nc$tick / 45.0f));
                } else {
                    stop(nc$sound);
                    setFade$nc(false);
                }
            } else {
                setFade$nc(false);
            }
        }
    }

    @Unique
    public void setFade$nc(boolean fading) {
        instanceBySource.get(SoundSource.MUSIC).stream().findFirst().ifPresent(s -> {
            if (!(s instanceof TrackerTicker.TrackAmbient)) {
                nc$sound = s;
            }
        });
        nc$tick = 45;
        nc$fading = fading;
    }

    @Inject(method = "play", at = @At("HEAD"), cancellable = true, remap = false)
    private void nc$play(SoundInstance sound, CallbackInfo ci) {
        if (TrackerController.shouldCancelSound(sound)) {
            ci.cancel();
        }
    }
}
