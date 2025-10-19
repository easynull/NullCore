package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OuterSound extends DeferredRegister<SoundEvent> {
    final String modid;
    private OuterSound(String namespace) {
        super(Registries.SOUND_EVENT, namespace);
        this.modid = namespace;
    }

    public static OuterSound create(String modId){
        return new OuterSound(modId);
    }

    public <S extends SoundEvent> DeferredHolder<SoundEvent, S> registerSound(String name) {
        return register(name, ()-> (S) SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modid, name)));
    }

    @Override
    public <S extends SoundEvent> DeferredHolder<SoundEvent, S> register(String name, Supplier<? extends S> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    protected <S extends SoundEvent> DeferredHolder<SoundEvent, S> createHolder(ResourceKey<? extends Registry<SoundEvent>> registryKey, ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(registryKey, key));
    }
}
