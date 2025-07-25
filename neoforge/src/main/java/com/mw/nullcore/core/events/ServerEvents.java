package com.mw.nullcore.core.events;

import com.mw.nullcore.Utils;
import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
        Utils.Client.tickServer(event.getServer());
    }
}
