package com.mw.nullcore.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RsSound {
    private final String modid;
    private final Map<ResourceLocation, SoundEvent> sounds = new HashMap<>();

    private RsSound(String modid) {
        this.modid = modid;
    }

    public static RsSound create(String modid) {
        return new RsSound(modid);
    }

    public <S extends SoundEvent> S registerSound(String id){
        S reg = (S) SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modid, id));
        sounds.put(ResourceLocation.fromNamespaceAndPath(modid, id), reg);
        return reg;
    }

    public void registerAll() {
        sounds.forEach((id, supplier) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, supplier));
    }
}
