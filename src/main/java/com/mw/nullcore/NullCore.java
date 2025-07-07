package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.NullEntities;
import com.mw.nullcore.core.holders.KeyRegisters;
import com.mw.nullcore.utils.ClientUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NullCore.ID)
public final class NullCore {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ID = "nullcore";

    public NullCore(IEventBus bus, ModContainer mod) {
        bus.addListener(this::client);
        bus.register(new KeyRegisters.Event());
        NullComponents.components.register(bus);
        NullEntities.entities.register(bus);
        mod.registerConfig(ModConfig.Type.COMMON, NullConfig.SPEC);
    }

    void client(final FMLClientSetupEvent event) {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::tickClient);
    }
}
