package com.mw.nullcore.core.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface CustomableName {
    @OnlyIn(Dist.CLIENT)
    Component getItemName(String pastName, ItemStack stack);
}
