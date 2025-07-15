package com.mw.nullcore.client.audio;

import com.mw.nullcore.Utils;
import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.mixin.SoundManagerAccessor;
import com.mw.nullcore.managers.TracksManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public final class TrackerTicker {
    final @NotNull Minecraft mc = Minecraft.getInstance();
    private TrackAmbient track;
    private Track selectedTrack;
    private int timeNextTrack = 300;
    private ResourceLocation currentStage;

    public TrackerTicker() {}

    public void tick(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        var id = getStage(level, player.blockPosition());

        if (selectedTrack == null || timeNextTrack == 0) {
            selectedTrack = null;
            var tracks = TracksManager.selfTracks.get(id);
            if (tracks != null) {
                selectedTrack = getTrack(tracks, level);
            }
        }
        if(id != currentStage) stop(level.random);
        currentStage = id;

        if (canPlay()) play(selectedTrack, level.random);
    }

    private ResourceLocation getStage(ServerLevel level, BlockPos pos) {
        for (Entity entity : Utils.Level.getEntities(level, pos, NullConfig.radiusEntityTrack.get())) {
            ResourceLocation entityId = EntityType.getKey(entity.getType());
            if (TracksManager.selfTracks.containsKey(entityId)) {
                return entityId;
            }
        }
        for (ResourceLocation structureId : TracksManager.selfTracks.keySet()) {
            if (Utils.Level.isPosInStructure(level, pos, structureId)) {
                return structureId;
            }
        }
        return level.dimension().location();
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

    private Track getTrack(HashSet<Track> trackList, ServerLevel level){
        var tl = trackList.stream().toList();
        return trackList.size() > 1 ? tl.get(level.random.nextInt(trackList.size())) : tl.get(0);
    }

    public void play(Track track, RandomSource rand) {
        ((FadeSoundEngine)((SoundManagerAccessor)mc.getSoundManager()).nc$getSoundEngine()).nc$fade(true);
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