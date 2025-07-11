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

    private static final ModConfigSpec.BooleanValue vfxConfig;
    private static final ModConfigSpec.IntValue radiusTrackEntityConfig;

    static {
        vfxConfig = builder.comment("Disables/enables rendering of overlays of some items in the inventory").define("VFXGui", true);
        radiusTrackEntityConfig = builder.comment("The value that determines the radius of the entity's location for playing its track").defineInRange("RadiusFindTrackEntity", 20, 0, 70);
        SPEC = builder.build();
    }

    public static boolean VFXGui;
    public static int radiusTrackEntity;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        VFXGui = vfxConfig.get();
        radiusTrackEntity = radiusTrackEntityConfig.get();
    }
}
