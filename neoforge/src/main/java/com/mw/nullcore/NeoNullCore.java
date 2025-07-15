package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.builders.CommandBuilder;
import com.mw.nullcore.core.items.CreativeContent;
import com.mw.nullcore.events.ClientEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.mw.nullcore.NullCore.ID;

@Mod(ID)
public final class NeoNullCore {
    public NeoNullCore(final IEventBus bus) {
        final IEventBus game = NeoForge.EVENT_BUS;
        NullCore.init();
        NeoPlatform.registerAll(bus);
        NullConfig.initialize(FMLPaths.CONFIGDIR.get());
        game.addListener(ClientEvents::tickClient);
        game.addListener((RegisterCommandsEvent event) -> CommandBuilder.registers(event.getDispatcher()));
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof CreativeContent cc) {
                    if (event.getTabKey() == cc.getCreativeTab()) cc.addContents(event);
                } else if (item instanceof BlockItem bi && bi.getBlock() instanceof CreativeContent cc) {
                    if (event.getTabKey() == cc.getCreativeTab()) cc.addContents(event);
                }
            }
        });
    }
}