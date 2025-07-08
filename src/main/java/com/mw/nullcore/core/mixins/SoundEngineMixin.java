package com.mw.nullcore.core.mixins;

import com.google.common.collect.Multimap;
import com.mw.nullcore.client.audio.FadeSoundEngine;
import com.mw.nullcore.client.audio.TrackerTicker;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements FadeSoundEngine {
    @Shadow public abstract void setVolume(SoundInstance sound, float volume);
    @Shadow @Final private Multimap<SoundSource, SoundInstance> instanceBySource;
    @Shadow public abstract void stop(@Nullable ResourceLocation soundName, @Nullable SoundSource category);

    @Shadow public abstract void stop(SoundInstance sound);

    @Unique private boolean nc$fading = false;
    @Unique private int nc$tick = 45;
    @Unique private SoundInstance nc$soundInstance;

    @Inject(method = "tick", at = @At("HEAD"))
    private void nc$onTick(CallbackInfo ci) {
        if (nc$fading) {
            if(nc$soundInstance != null){
                if (nc$tick > 0) {
                    --nc$tick;
                    setVolume(nc$soundInstance, Math.max(0, nc$tick / 45.0f));
                } else {
                    stop(nc$soundInstance);
                    nc$fade(false);
                }
            } else {
                nc$fade(false);
            }
        }
    }

    @Unique
    public void nc$fade(boolean fading) {
        if(instanceBySource.get(SoundSource.MUSIC).stream().findFirst().isPresent()){
            if(instanceBySource.get(SoundSource.MUSIC).stream().findFirst().get() instanceof SoundInstance sound && !(sound instanceof TrackerTicker.TrackAmbient)){
                nc$soundInstance = sound;
            }
        }
        nc$tick = 45;
        nc$fading = fading;
    }
}
