package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.NullComponents;
import com.mw.nullcore.core.NullEntities;
import com.mw.nullcore.core.blocks.TestBlock;
import com.mw.nullcore.core.blocks.type.TestBE;
import com.mw.nullcore.core.items.TestItem;
import com.mw.nullcore.core.holders.NullRegisters;
import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
        items.register(bus);
        blocks.register(bus);
        bes.register(bus);
        NullComponents.components.register(bus);
        NullEntities.entities.register(bus);
        mod.registerConfig(ModConfig.Type.COMMON, NullConfig.SPEC);
    }

    void client(final FMLClientSetupEvent event) {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::onTicker);
    }

    static final NullRegisters.AdItems items = new NullRegisters.AdItems(ID);
    static final NullRegisters.AdBlocks blocks = new NullRegisters.AdBlocks(ID, items);
    static final DeferredRegister<BlockEntityType<?>> bes = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ID);

    DeferredItem<TestItem> n = items.registerItem("null", TestItem::new);
    static DeferredBlock<Block> nn = blocks.registerBlock("null2", TestBlock::new);
    public static Supplier<BlockEntityType<TestBE>> null3 = bes.register("null", ()-> new BlockEntityType<>(TestBE::new, nn.get()));
}
