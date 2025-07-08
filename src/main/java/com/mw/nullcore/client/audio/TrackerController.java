package com.mw.nullcore.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class TrackerController {
    static final TrackerTicker ticker = new TrackerTicker();

    public static void tick(ServerPlayer player) {
        ticker.tick(player);
    }

    @SubscribeEvent
    public static void onTrackControl(PlaySoundEvent event) {
        if(event.getSound() == null) return;
        if(event.getSound().getSource() == SoundSource.MUSIC) {
            if (!ticker.isMusic(event.getSound()) && ticker.playingTrack()) {
                event.setSound(null);
            }
        }
    }
}