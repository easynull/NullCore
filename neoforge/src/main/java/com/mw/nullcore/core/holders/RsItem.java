package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class RsItem extends DeferredRegister<Item> {
    final String id;
    private RsItem(String modId) {
        super(Registries.ITEM, modId);
        this.id = modId;
    }

    public static RsItem create(String modId){
        return new RsItem(modId);
    }

    public <I extends Item > DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> item) {
        return register(name, () -> item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id, name)))));
    }

    @Override
    public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> sup) {
        return (DeferredItem<I>) this.register(name, key -> sup.get());
    }
    @Override
    protected <I extends Item> DeferredItem<I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, ResourceLocation key) {
        return DeferredItem.createItem(ResourceKey.create(registryKey, key));
    }
}