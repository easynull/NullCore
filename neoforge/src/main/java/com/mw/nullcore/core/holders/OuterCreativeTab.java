package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OuterCreativeTab extends DeferredRegister<CreativeModeTab> {
    private OuterCreativeTab(String modid) {
        super(Registries.CREATIVE_MODE_TAB, modid);
    }

    public static OuterCreativeTab create(String modid) {
        return new OuterCreativeTab(modid);
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(String name, Component title, Supplier<ItemStack> icon, ResourceLocation background, ItemLike... items){
        return register(name, ()-> CreativeModeTab.builder().title(title).icon(icon).displayItems((p, o) -> {
            for (ItemLike item : items) {
                if (item != null) o.accept(item);
            }
        }).backgroundTexture(background).build());
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerSearchTab(String name, Component title, Supplier<ItemStack> icon, ResourceLocation background, ItemLike... items){
        return register(name, ()-> CreativeModeTab.builder().title(title).icon(icon).displayItems((p, o) -> {
            for (ItemLike item : items) {
                if (item != null) o.accept(item);
            }
        }).backgroundTexture(background).withSearchBar().build());
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(String name, Component title, Supplier<ItemStack> icon, ItemLike... items){
        return registerTab(name, title, icon, CreativeModeTab.createTextureLocation("items"), items);
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> registerSearchTab(String name, Component title, Supplier<ItemStack> icon, ItemLike... items){
        return registerSearchTab(name, title, icon, CreativeModeTab.createTextureLocation("item_search"), items);
    }

    @Override
    public <S extends CreativeModeTab> DeferredHolder<CreativeModeTab, S> register(String name, Supplier<? extends S> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    protected <S extends CreativeModeTab> DeferredHolder<CreativeModeTab, S> createHolder(ResourceKey<? extends Registry<CreativeModeTab>> registryKey, ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(registryKey, key));
    }
}
