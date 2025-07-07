package com.mw.nullcore.core.holders;

import com.mw.nullcore.core.multiblocks.Blueprint;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static com.mw.nullcore.NullCore.ID;

public final class KeyRegisters {
    static final ResourceKey<Registry<Blueprint>> MULTIBLOCK = createKey("multiblock");
    public static final Registry<Blueprint> MULTIBLOCK_TYPE = new RegistryBuilder<>(MULTIBLOCK).create();

    private static <T> ResourceKey<Registry<T>> createKey(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ID, name));
    }

    public static class Event {
        @SubscribeEvent
        private void onRegisters(NewRegistryEvent event) {
            event.register(KeyRegisters.MULTIBLOCK_TYPE);
        }
    }
}
