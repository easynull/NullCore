package com.mw.nullcore.core.holders;

import com.mw.nullcore.core.multiblocks.Blueprint;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public record OuterMultiblock(String id) {
    public static final Map<ResourceLocation, Blueprint> multiblocks = new HashMap<>();

    public static OuterMultiblock create(String modid) {
        return new OuterMultiblock(modid);
    }

    public <B extends Blueprint> B registerMultiblock(String name, Supplier<B> multiblock) {
        multiblocks.put(ResourceLocation.fromNamespaceAndPath(id, name), multiblock.get());
        return multiblock.get();
    }

    public <B extends Blueprint> B get(ResourceLocation location) {
        return (B) multiblocks.get(location);
    }
}
