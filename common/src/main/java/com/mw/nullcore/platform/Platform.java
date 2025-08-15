package com.mw.nullcore.platform;

import java.util.ServiceLoader;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface Platform {
    Platform PLATFORM = load(Platform.class);

    static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    boolean isClient();

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value);

    void sendTo(Player player, CustomPacketPayload packet);
}