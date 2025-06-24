package com.mw.nullcore.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD)
public class NullConfig {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue buildGuiParticle;

    static {
        buildGuiParticle = builder.comment("Disables/enables rendering of particles of some items in the inventory").define("renderParticleGui", true);

        SPEC = builder.build();
    }

    public static boolean particleInGui;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        particleInGui = buildGuiParticle.get();
    }
}
