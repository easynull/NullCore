package com.mw.nullcore.core.events;

import com.mw.nullcore.core.level.BiomeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.GAME)
public final class ServerEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    private static void onResourceReload(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_manager"), new SimplePreparableReloadListener<Map<ResourceLocation, Long>>() {
            @Override
            protected Map<ResourceLocation, Long> prepare(ResourceManager manager, ProfilerFiller profiler) {
                return BiomeManager.loadRules(manager);
            }

            @Override
            protected void apply(Map<ResourceLocation, Long> rules, ResourceManager manager, ProfilerFiller profiler) {
                BiomeManager.applyRules(rules);
            }
        });
    }
}
