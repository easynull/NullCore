package com.mw.nullcore.core.events;

import com.mw.nullcore.client.audio.TrackerController;
import com.mw.nullcore.data.BiomeRulesManager;
import com.mw.nullcore.data.TracksManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.io.IOException;
import java.util.Map;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.GAME)
public final class ServerEvents {
    @SubscribeEvent
    private static void onResourceReload(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_rules"), new BiomeRulesManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "tracks"), new TracksManager());
    }

    @SubscribeEvent
    public static void tickServer(ServerTickEvent.Post event) {
        MinecraftServer mc = event.getServer();
        for(ServerPlayer player : mc.getPlayerList().getPlayers()) TrackerController.tick(player);
    }
}
