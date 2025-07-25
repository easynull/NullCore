package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;

public final class OuterSound {
    private final String modid;
    private final Map<ResourceLocation, SoundEvent> sounds = new HashMap<>();

    private OuterSound(String modid) {
        this.modid = modid;
    }

    public static OuterSound create(String modid) {
        return new OuterSound(modid);
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
