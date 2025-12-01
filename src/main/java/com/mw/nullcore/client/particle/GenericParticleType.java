package com.mw.nullcore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class GenericParticleType extends AbstractParticleType<GenericParticleOptions> {
    public record Factory(SpriteSet sprite) implements ParticleProvider<GenericParticleOptions> {
        @Override
        public Particle createParticle(GenericParticleOptions options, ClientLevel level, double x, double y, double z, double mx, double my, double mz) {
            return new GenericParticle(level, options, (ParticleEngine.MutableSpriteSet) sprite, x, y, z, mx, my, mz);
        }
    }
}
