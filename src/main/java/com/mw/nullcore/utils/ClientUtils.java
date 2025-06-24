package com.mw.nullcore.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ClientUtils {
    private final static Minecraft mc = Minecraft.getInstance();
    public static int clientTick;

    public static void onTicker(ClientTickEvent.Post event) {
        if (!mc.isPaused()) clientTick++;
    }

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
}
