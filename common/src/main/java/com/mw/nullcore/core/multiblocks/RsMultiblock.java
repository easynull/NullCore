package com.mw.nullcore.core.multiblocks;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class RsMultiblock {
    public static final Map<ResourceLocation, Blueprint> multiblocks = new ConcurrentHashMap<>();
    final private String id;

    private RsMultiblock(String id){
        this.id = id;
    }

    public static RsMultiblock create(String modid){
        return new RsMultiblock(modid);
    }

    public <B extends Blueprint> B registerBlueprint(String name, Supplier<B> multiblock) {
        multiblocks.put(ResourceLocation.fromNamespaceAndPath(id, name), multiblock.get());
        return multiblock.get();
    }
}
