package com.mw.nullcore;

import com.mojang.logging.LogUtils;
import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.builders.CommandBuilder;
import com.mw.nullcore.registers.NcComponents;
import com.mw.nullcore.registers.NcEntities;
import com.mw.nullcore.registers.NcEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(NullCore.ID)
public final class NullCore {
    public static final String ID = "nullcore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NullCore(final IEventBus bus) {
        final IEventBus game = NeoForge.EVENT_BUS;
        NcComponents.components.register(bus);
        NcEntities.entities.register(bus);
        NcConfig.register();
        game.addListener((RegisterCommandsEvent event) -> CommandBuilder.registerAll(event.getDispatcher()));
        game.addListener(NcEvents::onRegistryResource);
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String id) {
        return ResourceKey.create(key, path(id));
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String modid, String id) {
        return ResourceKey.create(key, ResourceLocation.fromNamespaceAndPath(modid, id));
    }
}
