package com.mw.nullcore.client.audio;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.mixins.SoundManagerAccessor;
import com.mw.nullcore.data.TracksManager;
import com.mw.nullcore.utils.WorldUtils;
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

import java.util.HashSet;
import java.util.List;

public final class TrackerTicker {
    final Minecraft mc = Minecraft.getInstance();
    private TrackAmbient track;
    private Track selectedTrack;
    private int timeNextTrack = 300;
    private ResourceLocation currentStructure;

    public TrackerTicker() {}

    public void tick(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        var newStructure = getCurrentStructure(level, player.blockPosition());

        if (currentStructure != null && newStructure == null) {
            stop();
        }
        currentStructure = newStructure;
        if (selectedTrack == null || timeNextTrack == 0) updateTrack(level, newStructure);

        if (canPlay()) {
            play(selectedTrack, level.random);
            NullCore.LOGGER.info("Track {} starting play!", track.getLocation());
        } else if (selectedTrack == null) {
            stop();
        }
    }

    private void updateTrack(ServerLevel level, ResourceLocation id) {
        selectedTrack = null;
        HashSet<Track> structureTracks = TracksManager.selfTracks.get(id);
        if(structureTracks != null) {
            selectedTrack = getTrack(structureTracks, level);
        } else {
            HashSet<Track> worldTracks = TracksManager.selfTracks.get(level.dimension().location());
            if (worldTracks != null) {
                selectedTrack = getTrack(worldTracks, level);
            }
        }
    }

    private ResourceLocation getCurrentStructure(ServerLevel level, BlockPos pos) {
        for (ResourceLocation structureId : TracksManager.selfTracks.keySet()) {
            if (WorldUtils.isPosInStructure(level, pos, structureId)) {
                return structureId;
            }
        }
        return null;
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
        List<Track> tl = trackList.stream().toList();
        return trackList.size() > 1 ? tl.get(level.random.nextInt(trackList.size())) : tl.get(0);
    }

    public void play(Track track, RandomSource rand) {
        this.track = new TrackAmbient(track.getSound(), SoundSource.MUSIC);
        this.track.setVolume(1.0f);
        this.track.setTick(45);
        ((FadeSoundEngine)((SoundManagerAccessor)mc.getSoundManager()).getSoundEngine()).nc$fade(true);
        mc.getSoundManager().play(this.track);
        timeNextTrack = Mth.nextInt(rand, track.minDelay(), track.maxDelay());
    }

    public void stop() {
        if (track != null) {
            track.fadeAway = false;
            track = null;
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
        public boolean fadeAway = true;

        protected TrackAmbient(SoundEvent sound, SoundSource source) {
            super(sound, source, SoundInstance.createUnseededRandom());
        }

        @Override
        public void tick() {
            if (tick >= 0) {
                if (fadeAway) {
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