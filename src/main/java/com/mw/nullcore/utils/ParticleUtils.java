package com.mw.nullcore.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ParticleUtils {
    public static void forParticleSpawn(Level level, ParticleOptions particle, float pX, float pY, float pZ, float xSpeed, float ySpeed, float zSpeed, int count) {
        RandomSource rand = level.random;
        for (int i = 0; i < count; i++) {
            double oX = (rand.nextDouble() - 0.5) * 0.5;
            double oY = (rand.nextDouble() - 0.5) * 0.5;
            double oZ = (rand.nextDouble() - 0.5) * 0.5;
            level.addParticle(particle, pX + oX, pY + oY, pZ + oZ, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void forParticleSpawn(Level level, ParticleOptions particle, float pX, float pY, float pZ, int count) {
        forParticleSpawn(level, particle, pX, pY, pZ, 0, 0, 0, count);
    }

    public static void writeParticle(CompoundTag tag, ParticleOptions particle) {
        CompoundTag pTag = new CompoundTag();
        ResourceLocation loc = BuiltInRegistries.PARTICLE_TYPE.getKey(particle.getType());
        pTag.putString("particle_namespace", loc.getNamespace());
        pTag.putString("particle_path", loc.getPath());
        tag.put("particle", pTag);
    }

    public static ParticleOptions readParticle(CompoundTag tag) {
        CompoundTag pTag = (CompoundTag) tag.get("particle");
        return (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.getOptional(ResourceLocation.fromNamespaceAndPath(pTag.getString("particle_namespace"), pTag.getString("particle_path"))).get();
    }
}
