package com.mw.nullcore;

import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Nullcore.ID)
public final class Nullcore {
    public static final String ID = "nullcore";

    public Nullcore(IEventBus bus) {
        bus.addListener(this::start);
        bus.addListener(this::client);
        items.register(bus);
    }

    void start(final FMLCommonSetupEvent event) {

    }

    void client(final FMLClientSetupEvent event){
        var bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientUtils::onTicker);
    }

    private static final DeferredRegister.Items items = DeferredRegister.createItems(ID);
    DeferredItem<TestItem> t = items.register("tester", ()-> new TestItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ID, "tester")))));
}
