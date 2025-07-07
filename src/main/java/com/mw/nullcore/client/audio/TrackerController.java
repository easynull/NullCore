package com.mw.nullcore.client.audio;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.data.TracksManager;
import com.mw.nullcore.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.server.MinecraftServer;
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
    static final Minecraft mc = Minecraft.getInstance();
    static final TrackerTicker ticker = new TrackerTicker();
    static ServerLevel level;

    public static void tick(ServerPlayer player) {
        ticker.tick(player);
        level = (ServerLevel) player.level();
    }

    @SubscribeEvent
    public static void onTrackControl(PlaySoundEvent event) {
        if(event.getSound() == null) return;
        if(event.getSound().getSource() == SoundSource.MUSIC) {
            if (!ticker.isMusic(event.getSound()) && ticker.playingMusic()) {
                event.setSound(null);
            }
        }
    }
}