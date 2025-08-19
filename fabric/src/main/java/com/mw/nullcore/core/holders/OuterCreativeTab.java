package com.mw.nullcore.core.holders;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class OuterCreativeTab {
    private final String modid;
    private final Map<ResourceLocation, CreativeModeTab> types = new HashMap<>();

    private OuterCreativeTab(String modid) {
        this.modid = modid;
    }

    public static OuterCreativeTab create(String modid) {
        return new OuterCreativeTab(modid);
    }

    public CreativeModeTab registerTab(String id, Component title, Supplier<ItemStack> icon, ResourceLocation background, ItemLike... items){
        CreativeModeTab reg = FabricItemGroup.builder().title(title).icon(icon).displayItems((p, o) -> {
            for (ItemLike item : items) {
                if (item != null) o.accept(item);
            }
        }).backgroundTexture(background).build();
        types.put(ResourceLocation.fromNamespaceAndPath(modid, id), reg);
        return reg;
    }

    public CreativeModeTab registerTab(String id, Component title, Supplier<ItemStack> icon, ItemLike... items){
        return registerTab(id, title, icon, CreativeModeTab.createTextureLocation("items"), items);
    }

    public void registerAll() {
        types.forEach((id, supplier) -> Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, supplier));
    }
}
