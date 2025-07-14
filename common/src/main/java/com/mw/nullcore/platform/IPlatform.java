package com.mw.nullcore.platform;

import java.util.ServiceLoader;
import java.util.function.Supplier;

import com.mw.nullcore.NullCore;
import net.minecraft.core.Registry;

public interface IPlatform {
    IPlatform PLATFORM = load(IPlatform.class);

    static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value);
}