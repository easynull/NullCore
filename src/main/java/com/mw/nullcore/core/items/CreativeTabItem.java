package com.mw.nullcore.core.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public interface CreativeTabItem {
    void addCreativeTab(ResourceKey<CreativeModeTab> tab, Collection<ItemStack> output);
}
