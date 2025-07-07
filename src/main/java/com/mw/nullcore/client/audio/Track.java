package com.mw.nullcore.client.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record Track(ResourceLocation sound, int minDelay, int maxDelay) {
    public static Codec<Track> codec = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("sound").forGetter(Track::sound),
                    Codec.INT.fieldOf("min_delay").orElse(1200).forGetter(Track::minDelay),
                    Codec.INT.fieldOf("max_delay").orElse(1600).forGetter(Track::maxDelay)
            ).apply(instance, Track::new));

    public SoundEvent getSound() {
        return BuiltInRegistries.SOUND_EVENT.getValue(sound);
    }
}
