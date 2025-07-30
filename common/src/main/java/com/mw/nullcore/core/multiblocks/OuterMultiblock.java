package com.mw.nullcore.core.multiblocks;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class OuterMultiblock {
    public static final Map<ResourceLocation, Blueprint> multiblocks = new ConcurrentHashMap<>();
    final private String id;

    private OuterMultiblock(String id){
        this.id = id;
    }

    public static OuterMultiblock create(String modid){
        return new OuterMultiblock(modid);
    }

    public <B extends Blueprint> B registerBlueprint(String name, Supplier<B> multiblock) {
        multiblocks.put(ResourceLocation.fromNamespaceAndPath(id, name), multiblock.get());
        return multiblock.get();
    }
}
