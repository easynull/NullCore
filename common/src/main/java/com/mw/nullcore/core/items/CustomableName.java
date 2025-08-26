package com.mw.nullcore.core.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface CustomableName {
    Component getItemName(String pastName, ItemStack stack);
}
