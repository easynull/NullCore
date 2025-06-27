package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.NullEntities;
import com.mw.nullcore.core.holders.RsBlocks;
import com.mw.nullcore.core.holders.RsItems;
import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(NullCore.ID)
public final class NullCore {
    public static final String ID = "nullcore";

    public NullCore(IEventBus bus, ModContainer mod) {
        bus.addListener(this::client);
        NullComponents.components.register(bus);
        NullEntities.entities.register(bus);
        mod.registerConfig(ModConfig.Type.COMMON, NullConfig.SPEC);
    }

    void client(final FMLClientSetupEvent event) {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::onTicker);
    }
}
