package com.mw.nullcore.client.particle.screen;

import com.mw.nullcore.client.render.NcRenderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ScreenParticleHolder {
    public final Map<NcRenderType, ArrayList<ScreenParticle>> particles = new HashMap<>();
    public void tick() {
        particles.forEach((pair, particles) -> {
            Iterator<ScreenParticle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                ScreenParticle particle = iterator.next();
                particle.tick();
                if (!particle.isAlive()) {
                    iterator.remove();
                }
            }
        });
    }

    public void addFrom(ScreenParticleHolder otherHolder) {
        particles.putAll(otherHolder.particles);
    }

    public boolean isEmpty() {
        return particles.values().stream().allMatch(ArrayList::isEmpty);
    }
}
