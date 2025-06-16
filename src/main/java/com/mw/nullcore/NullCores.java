package com.mw.nullcore;

import com.mw.nullcore.core.components.NullComponents;
import com.mw.nullcore.utils.ClientUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(NullCores.ID)
public final class NullCores {
    public static final String ID = "nullcore";

    public NullCores(IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::client);
        NullComponents.components.register(bus);
    }

    void setup(final FMLCommonSetupEvent event) {
    }

    void client(final FMLClientSetupEvent event) {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::onTicker);
    }
}
