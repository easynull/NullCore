package com.mw.nullcore.client.audio;

import com.mw.nullcore.core.managers.TracksManager;
import com.mw.nullcore.core.mixin.SoundManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
public final class TrackerTicker {
    final static Minecraft mc = Minecraft.getInstance();
    private TrackAmbient track;
    private Track selectedTrack;
    private int timeNextTrack = 300;
    public static ResourceLocation currentStage, oldStage;

    public TrackerTicker() {}

    public void tick() {
        if (mc.player == null || mc.level == null) return;

        if (oldStage != null && !oldStage.equals(currentStage)) {
            stop(mc.level.random);
        }

        if (selectedTrack == null || timeNextTrack == 0) {
            selectedTrack = null;
            var tracks = TracksManager.selfTracks.get(currentStage);
            if (tracks != null) {
                selectedTrack = getTrack(tracks, mc.level);
            }
        }

        if (canPlay()) play(selectedTrack, mc.level.random);
        oldStage = currentStage;
    }

    private boolean canPlay() {
        if (selectedTrack == null) return false;
        if (track != null && mc.getSoundManager().isActive(track)) return false;

        if (timeNextTrack > 0) {
            timeNextTrack--;
            return false;
        }
        return true;
    }

    private static Track getTrack(HashSet<Track> trackList, Level level){
        var tl = trackList.stream().toList();
        return trackList.size() > 1 ? tl.get(level.random.nextInt(trackList.size())) : tl.getFirst();
    }

    public void play(Track track, RandomSource rand) {
        ((Fading)((SoundManagerAccessor)mc.getSoundManager()).getSoundEngine()).setFade(true);
        this.track = new TrackAmbient(track.getSound(), SoundSource.MUSIC);
        this.track.setTick(45);
        mc.getSoundManager().play(this.track);
        timeNextTrack = Mth.nextInt(rand, track.minDelay(), track.maxDelay());
    }

    public void stop(RandomSource rand) {
        if (track != null) {
            track.fade = true;
            track = null;
            timeNextTrack = Mth.nextInt(rand, 100, 300);
        }
    }

    public boolean playingTrack() {
        return track != null;
    }

    public boolean isMusic(SoundInstance sound) {
        return track == sound;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TrackAmbient extends AbstractTickableSoundInstance {
        private int tick;
        public boolean fade;

        protected TrackAmbient(SoundEvent sound, SoundSource source) {
            super(sound, source, SoundInstance.createUnseededRandom());
        }

        @Override
        public void tick() {
            if (tick >= 0) {
                if (!fade) {
                    ++tick;
                } else {
                    --tick;
                }
                setTick(Math.min(tick, 45));
                setVolume(Math.max(0, Math.min(tick / 45.0f, 1.0f)));
            } else {
                stop();
            }
        }

        public void setVolume(float volume){
            this.volume = volume;
        }

        public void setTick(int tick){
            this.tick = tick;
        }
    }
}