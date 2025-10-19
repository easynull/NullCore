package com.mw.nullcore.client.particle.screen;

import net.minecraft.client.multiplayer.ClientLevel;

public class ScreenParticleType<T extends ScreenParticleOptions> {
    public ParticleProvider<T> provider;

    public interface ParticleProvider<T extends ScreenParticleOptions> {
        ScreenParticle createParticle(ClientLevel level, T options, double x, double y, double xSpeed, double ySpeed);
    }
}
