package com.mw.nullcore.core.events;

import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.GAME)
public final class ServerEvents {
    @SubscribeEvent
    private static void onResourceReload(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_rules"), new BiomeRulesManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "tracks"), new TracksManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "lockable"), new LockableManager());
    }
}
