package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.items.CreativeTabItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeTab.class)
public final class CreativeTabMixin {
    @Shadow private Collection<ItemStack> displayItems;

    @Inject(method = "buildContents", at = @At("TAIL"), remap = false)
    private void nc$buildContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab)(Object)this;
        ResourceKey<CreativeModeTab> key = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).get();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof CreativeTabItem creativeItem) {
                creativeItem.addCreativeTab(key, this.displayItems);
            }
        }
    }
}
