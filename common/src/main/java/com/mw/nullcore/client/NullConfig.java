package com.mw.nullcore.client;

import com.mw.nullcore.managers.ConfigManager;

import java.nio.file.Path;

public final class NullConfig {
    public static ConfigManager.Initialize.Unit<Boolean> enableGuiVFX;
    public static ConfigManager.Initialize.Unit<Boolean> enableMassageUpdate;
    public static ConfigManager.Initialize.Unit<Byte> radiusEntityTrack;

    public static void initialize(Path configDir){
        ConfigManager.initialize(configDir, ()-> {
            enableGuiVFX = ConfigManager.create(
                    "enable_gui_vfx",
                    "Disables/enables rendering of overlays of some items in the inventory",
                    true
            );
            enableMassageUpdate = ConfigManager.create(
                    "enable_massage_update",
                    "Disables/enables mod update notification",
                    true
            );
            radiusEntityTrack = ConfigManager.create(
                    "radius_entity_track",
                    "The value that determines the radius of the entity location for playing its track",
                    (byte) 22
            );
        });
    }
}
