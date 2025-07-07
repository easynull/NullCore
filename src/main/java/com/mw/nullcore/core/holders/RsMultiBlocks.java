package com.mw.nullcore.core.holders;

import com.mw.nullcore.core.multiblocks.Blueprint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class RsMultiBlocks extends DeferredRegister<Blueprint> {
    final String id;
    private RsMultiBlocks(String modId) {
        super(KeyRegisters.MULTIBLOCK, modId);
        this.id = modId;
    }

    public static RsMultiBlocks create(String modId){
        return new RsMultiBlocks(modId);
    }

    @Override
    public <B extends Blueprint> DeferredHolder<Blueprint, B> register(String name, Supplier<? extends B> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    protected <B extends Blueprint> DeferredHolder<Blueprint, B> createHolder(ResourceKey<? extends Registry<Blueprint>> registryKey, ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(registryKey, key));
    }
}
