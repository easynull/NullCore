package com.mw.nullcore.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class AbstractParticleType<T extends GenericParticleOptions> extends ParticleType<T> {

    public AbstractParticleType(){
        super(false);
    }

    @Override
    public MapCodec<T> codec(){
        return genericCodec(this);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return null;
    }

    public static <K extends GenericParticleOptions> MapCodec<K> genericCodec(ParticleType<K> type){
        return MapCodec.unit(() -> (K)new GenericParticleOptions(type));
    }
}
