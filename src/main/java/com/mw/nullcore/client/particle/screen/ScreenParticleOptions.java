package com.mw.nullcore.client.particle.screen;

import com.mw.nullcore.client.render.NcRenderType;
import com.mw.nullcore.client.particle.data.ColorParticleData;
import com.mw.nullcore.client.particle.data.GenericParticleData;
import com.mw.nullcore.client.particle.data.SpinParticleData;
import com.mw.nullcore.client.particle.data.SpriteParticleData;

import java.awt.*;
import java.util.function.Consumer;

public final class ScreenParticleOptions {
    public final ScreenParticleType<?> type;
    public static final ColorParticleData DEFAULT_COLOR = ColorParticleData.create(Color.white, Color.white).build();
    public static final GenericParticleData DEFAULT_GENERIC = GenericParticleData.create(1, 0).build();
    public static final SpinParticleData DEFAULT_SPIN = SpinParticleData.create(0).build();
    public static final SpriteParticleData DEFAULT_SPRITE = SpriteParticleData.RANDOM;

    public ColorParticleData colorData = DEFAULT_COLOR;
    public GenericParticleData transparencyData = DEFAULT_GENERIC;
    public GenericParticleData scaleData = DEFAULT_GENERIC;
    public SpinParticleData spinData = DEFAULT_SPIN;

    public NcRenderType renderType = NcRenderType.ADDITIVE;
    public SpriteParticleData spriteData = DEFAULT_SPRITE;
    public Consumer<GenericScreenParticle> actor;

    public int lifetime = 20;
    public int additionalLifetime = 0;
    public float gravity = 0;
    public float additionalGravity = 0;

    public boolean tracksStack;
    public double stackTrackXOffset;
    public double stackTrackYOffset;

    public ScreenParticleOptions(ScreenParticleType<?> type) {
        this.type = type;
    }
}
