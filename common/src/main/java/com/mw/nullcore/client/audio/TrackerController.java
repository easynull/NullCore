package com.mw.nullcore.client.audio;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public final class TrackerController {
    public static final TrackerTicker ticker = new TrackerTicker();

    public static void tick() {
        ticker.tick();
    }

    public static boolean shouldCancelSound(SoundInstance sound) {
        if (sound == null) return false;
        return sound.getSource() == SoundSource.MUSIC && !ticker.isMusic(sound) && ticker.playingTrack();
    }
}