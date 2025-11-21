package com.mw.nullcore;

import com.mojang.logging.LogUtils;
import com.mw.nullcore.client.particle.screen.ParticleEmitterHandler;
import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.builders.CommandBuilder;
import com.mw.nullcore.core.items.CreativeContent;
import com.mw.nullcore.core.items.Renderable;
import com.mw.nullcore.registers.NcComponents;
import com.mw.nullcore.registers.NcEntities;
import com.mw.nullcore.registers.NcEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(NullCore.ID)
public final class NullCore {
    public static final String ID = "nullcore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NullCore(IEventBus bus) {
        final IEventBus game = NeoForge.EVENT_BUS;
        NcComponents.components.register(bus);
        NcEntities.entities.register(bus);
        NcConfig.init();
        game.addListener((RegisterCommandsEvent event) -> CommandBuilder.registerAll(event.getDispatcher()));
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            for (Item item : BuiltInRegistries.ITEM) {
                NcUtils.Item.instanceOf(item, CreativeContent.class, cc -> {
                    if (event.getTabKey() == cc.getCreativeTab()) cc.addContents(event);
                });
            }
        });
        game.addListener(NcEvents::onResourceReload);
        bus.addListener(this::client);
//        OuterItem items = OuterItem.create(ID);
//        items.registerItem("test", TestItem::new);
//        items.register(bus);
    }

    public void client(final FMLClientSetupEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof Renderable supplier) {
                ParticleEmitterHandler.registerEmitters(item, supplier);
            }
        }
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String id) {
        return ResourceKey.create(key, path(id));
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String modid, String id) {
        return ResourceKey.create(key, ResourceLocation.fromNamespaceAndPath(modid, id));
    }
}
