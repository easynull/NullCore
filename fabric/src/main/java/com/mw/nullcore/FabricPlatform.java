package com.mw.nullcore;

import java.util.function.Supplier;
import com.mw.nullcore.platform.IPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

public final class FabricPlatform implements IPlatform {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> supplier) {
        T reg = Registry.register(registry, NullCore.defaultLoc(name), supplier.get());
        return () -> reg;
    }
}
