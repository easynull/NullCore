package com.mw.nullcore.client.audio;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.data.TracksManager;
import com.mw.nullcore.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Map;

public final class TrackerTicker {
    final Minecraft mc = Minecraft.getInstance();
    public MusicAmbient music;
    public int timeNextMusic = 300;

    public TrackerTicker() {}

    public void tick(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        ResourceLocation currentWorld = level.dimension().location();
        boolean inStructure = false;
        Track selectedTrack = null;

        for (Map.Entry<ResourceLocation, List<Track>> entry : TracksManager.selfTracks.entrySet()) {
            if (WorldUtils.isPosInStructure(level, player.blockPosition(), entry.getKey())) {
                inStructure = true;
                selectedTrack = getTrack(entry.getValue(), level);
                break;
            }
        }
        if (!inStructure) {
            List<Track> worldTracks = TracksManager.selfTracks.get(currentWorld);
            if (worldTracks != null && !worldTracks.isEmpty()) {
                selectedTrack = getTrack(worldTracks, level);
            }
        }

        if (music != null) {
            if (!music.getLocation().equals(selectedTrack != null ? selectedTrack.sound() : null)) {
                stopMusic();
            }
        }

        if (selectedTrack != null && canPlayTrack(level.random, selectedTrack)) {
            playMusic(selectedTrack.getSound(), selectedTrack, level.random);
            NullCore.LOGGER.info("Playing {} track: {}", inStructure ? "structure" : "world", selectedTrack.sound());
        } else if (selectedTrack == null) {
            stopMusic();
        }
    }

    private boolean canPlayTrack(RandomSource rand, Track track) {
        if (music != null && mc.getSoundManager().isActive(music)) {
            return false;
        }
        if (timeNextMusic > 0) {
            timeNextMusic--;
            return false;
        }
        timeNextMusic = Mth.nextInt(rand, track.minDelay(), track.maxDelay());
        return true;
    }

    private Track getTrack(List<Track> trackList, ServerLevel level){
        return trackList.size() > 1 ? trackList.get(level.random.nextInt(trackList.size())) : trackList.get(0);
    }

    public boolean playingMusic() {
        return music != null;
    }

    public boolean isMusic(SoundInstance sound) {
        return music == sound;
    }

    public void playMusic(SoundEvent sound, Track track, RandomSource rand) {
        music = new MusicAmbient(sound, SoundSource.MUSIC);
        music.setVolume(1.0f);
        music.setTicks((short) 40);
        mc.getSoundManager().play(music);
        timeNextMusic = Math.min(Mth.nextInt(rand, track.minDelay(), track.maxDelay()), this.timeNextMusic);
    }

    public void stopMusic() {
        if (music != null) {
            music.shouldNotFade = false;
            music = null;
        }
    }

    public static class MusicAmbient extends AbstractTickableSoundInstance {
        public int ticksPlayed;
        public boolean shouldNotFade = true;

        protected MusicAmbient(SoundEvent sound, SoundSource source) {
            super(sound, source, SoundInstance.createUnseededRandom());
        }

        @Override
        public void tick() {
            if (this.ticksPlayed >= 0) {
                if (this.shouldNotFade) {
                    ++this.ticksPlayed;
                } else {
                    --this.ticksPlayed;
                }
                this.setTicks((short) Math.min(this.ticksPlayed, 40));
                this.volume = Math.max(0.0F, Math.min((float)this.ticksPlayed / 40.0F, 1.0F));
            } else {
                this.stop();
            }
        }

        public void setVolume(float v){
            this.volume = v;
        }

        public void setTicks(short t){
            this.ticksPlayed = t;
        }
    }
}
