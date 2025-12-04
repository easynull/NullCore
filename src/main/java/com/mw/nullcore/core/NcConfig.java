package com.mw.nullcore.core;

import com.mw.nullcore.core.managers.ConfigManager;

public final class NcConfig {
    public static ConfigManager.Initialize.Unit<Boolean> enableGuiVFX;
    public static ConfigManager.Initialize.Unit<Byte> radiusEntityTrack;

    public static void register() {
        ConfigManager.register("nullcore", () -> {
            enableGuiVFX = ConfigManager.create(
                    "enable_gui_vfx",
                    "Disables/enables rendering of overlays of some items in the inventory",
                    true
            );
            radiusEntityTrack = ConfigManager.create(
                    "radius_entity_track",
                    "The value that determines the radius of the entity location for playing its track",
                    (byte) 22
            );
        }, true);
    }
}