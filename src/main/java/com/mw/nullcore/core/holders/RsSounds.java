package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class RsSounds extends DeferredRegister<SoundEvent> {
    final String id;
    private RsSounds(String namespace) {
        super(Registries.SOUND_EVENT, namespace);
        this.id = namespace;
    }

    public static RsSounds create(String modId){
        return new RsSounds(modId);
    }

    public <S extends SoundEvent> DeferredHolder<SoundEvent, S> registerSound(String sound) {
        return register(sound, ()-> (S) SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(id, sound)));
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
