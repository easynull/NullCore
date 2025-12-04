package com.mw.nullcore.core.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public interface CreativeContent {
    void addContents(final BuildCreativeModeTabContentsEvent builder);
    ResourceKey<CreativeModeTab> getCreativeTab();
}
