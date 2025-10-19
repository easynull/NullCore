package com.mw.nullcore.client.particle;

import com.mw.nullcore.client.particle.data.ColorParticleData;
import com.mw.nullcore.client.particle.data.GenericParticleData;
import com.mw.nullcore.client.particle.data.SpinParticleData;
import com.mw.nullcore.client.particle.data.SpriteParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class GenericParticleOptions implements ParticleOptions {
    ParticleType<?> type;

    public static final ColorParticleData DEFAULT_COLOR = ColorParticleData.create(Color.white, Color.white).build();
    public static final GenericParticleData DEFAULT_GENERIC = GenericParticleData.create(1, 0).build();
    public static final SpinParticleData DEFAULT_SPIN = SpinParticleData.create(0).build();
    public static final SpriteParticleData DEFAULT_SPRITE = SpriteParticleData.RANDOM;

    public ColorParticleData colorData = DEFAULT_COLOR;
    public GenericParticleData transparencyData = DEFAULT_GENERIC;
    public GenericParticleData scaleData = DEFAULT_GENERIC;
    public SpinParticleData spinData = DEFAULT_SPIN;
    public SpriteParticleData spriteData = DEFAULT_SPRITE;

    public final Collection<Consumer<GenericParticle>> tickActors = new ArrayList<>();
    public final Collection<Consumer<GenericParticle>> spawnActors = new ArrayList<>();
    public final Collection<Consumer<GenericParticle>> renderActors = new ArrayList<>();

    public enum DiscardFunctionType{
        NONE, INVISIBLE, ENDING_CURVE_INVISIBLE
    }

    public DiscardFunctionType discardFunctionType = DiscardFunctionType.INVISIBLE;

    public int lifetime = 20;
    public int additionalLifetime = 0;
    public float gravity = 0;
    public float additionalGravity = 0;
    public float friction = 0.98f;
    public float additionalFriction = 0;
    public boolean shouldCull = false;
    public boolean shouldRenderTraits = true;
    public boolean hasPhysics = true;

    public GenericParticleOptions(ParticleType<?> type){
        this.type = type;
    }

    @Override
    public ParticleType<?> getType(){
        return type;
    }
}
