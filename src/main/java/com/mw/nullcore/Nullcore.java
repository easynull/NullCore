package com.mw.nullcore;

import com.mw.nullcore.core.components.NullComponents;
import com.mw.nullcore.utils.ClientUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Nullcore.ID)
public final class Nullcore {
    public static final String ID = "nullcore";

    public Nullcore(IEventBus bus) {
        bus.addListener(this::client);
        NullComponents.components.register(bus);
    }

    void client(final FMLClientSetupEvent event) {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::onTicker);
    }
}
