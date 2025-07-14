package com.mw.nullcore.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RsItem {
    private final String modid;
    private final Map<ResourceLocation, Item> items = new HashMap<>();

    private RsItem(String modid) {
        this.modid = modid;
    }

    public static RsItem create(String modid) {
        return new RsItem(modid);
    }

    public <I extends Item> I registerItem(String id,Function<Item.Properties, ? extends I> item){
        I reg = item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, id))));
        items.put(ResourceLocation.fromNamespaceAndPath(modid, id), reg);
        return reg;
    }

    public void registerAll() {
        items.forEach((id, supplier) -> Registry.register(BuiltInRegistries.ITEM, id, supplier));
    }
}
