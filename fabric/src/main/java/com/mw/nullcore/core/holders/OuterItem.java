package com.mw.nullcore.core.holders;

import com.mw.nullcore.NullCore;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OuterItem {
    private final String modid;
    private final Map<ResourceLocation, Item> items = new HashMap<>();

    private OuterItem(String modid) {
        this.modid = modid;
    }

    public static OuterItem create(String modid) {
        return new OuterItem(modid);
    }

    public <I extends Item> I registerItem(String name, Function<Item.Properties, ? extends I> item){
        I reg = item.apply(new Item.Properties().setId(NullCore.key(Registries.ITEM, modid, name)));
        items.put(ResourceLocation.fromNamespaceAndPath(modid, name), reg);
        return reg;
    }

    public <I extends Item> I register(String name, Supplier<I> item){
        Item reg = item.get();
        items.put(ResourceLocation.fromNamespaceAndPath(modid, name), reg);
        return (I) reg;
    }

    public void registerAll() {
        items.forEach((id, supplier) -> Registry.register(BuiltInRegistries.ITEM, id, supplier));
    }
}
