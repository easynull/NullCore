package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.builders.CommandBuilder;
import com.mw.nullcore.events.ClientEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.mw.nullcore.NullCore.ID;

@Mod(ID)
public final class NeoNullCore {
    public NeoNullCore(final IEventBus bus) {
        IEventBus game = NeoForge.EVENT_BUS;
        NullCore.init();
        NeoPlatform.registerAll(bus);
        game.addListener(ClientEvents::tickClient);
        game.addListener((RegisterCommandsEvent event)-> CommandBuilder.registers(event.getDispatcher()));
        NullConfig.initialize(FMLPaths.CONFIGDIR.get());
    }
}