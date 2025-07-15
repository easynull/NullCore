package com.mw.nullcore.core.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public interface CreativeContent {
    void addContents(CreativeModeTab.Output output);
    ResourceKey<CreativeModeTab> getCreativeTab();
}
