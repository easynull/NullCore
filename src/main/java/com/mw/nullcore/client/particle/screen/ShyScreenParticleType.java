package com.mw.nullcore.client.particle.screen;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;

public final class ShyScreenParticleType extends ScreenParticleType<ScreenParticleOptions> {
    public record Factory(SpriteSet sprite) implements ParticleProvider<ScreenParticleOptions> {
        @Override
        public ScreenParticle createParticle(ClientLevel pLevel, ScreenParticleOptions options, double x, double y, double pXSpeed, double pYSpeed) {
            return new GenericScreenParticle(pLevel, options, (ParticleEngine.MutableSpriteSet) sprite, x, y, pXSpeed, pYSpeed);
        }
    }
}