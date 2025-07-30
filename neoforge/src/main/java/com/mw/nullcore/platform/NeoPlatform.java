package com.mw.nullcore.platform;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import java.util.Map;

import static com.mw.nullcore.NullCore.ID;

public final class NeoPlatform implements Platform {
    private static final Map<ResourceKey<?>, DeferredRegister> cache = new Reference2ObjectOpenHashMap<>();

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
        return cache.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), ID)).register(name, value);
    }

    public static void registerAll(final IEventBus bus) {
        cache.values().forEach(deferredRegister -> deferredRegister.register(bus));
    }
}