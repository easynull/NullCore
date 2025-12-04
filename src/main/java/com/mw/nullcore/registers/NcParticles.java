package com.mw.nullcore.registers;

import com.mw.nullcore.client.particle.screen.ScreenParticleOptions;
import com.mw.nullcore.client.particle.screen.ScreenParticleType;
import com.mw.nullcore.client.particle.screen.ShyScreenParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public final class NcParticles {
    public static final ArrayList<ScreenParticleType<?>> screenParticleTypes = new ArrayList<>();

//    public static ScreenParticleType<ScreenParticleOptions> WISP = registerType(new ShyScreenParticleType());
//
//    public static void registerProvider() {
//        registerProvider(WISP, new ShyScreenParticleType.Factory(getSpriteSet(ResourceLocation.withDefaultNamespace("smoke"))));
//    }

    public static <T extends ScreenParticleOptions> ScreenParticleType<T> registerType(ScreenParticleType<T> type) {
        screenParticleTypes.add(type);
        return type;
    }

    public static <T extends ScreenParticleOptions> void registerProvider(ScreenParticleType<T> type, ScreenParticleType.ParticleProvider<T> provider) {
        type.provider = provider;
    }

    public static SpriteSet getSpriteSet(ResourceLocation tex) {
        return Minecraft.getInstance().particleEngine.spriteSets.get(tex);
    }
}
