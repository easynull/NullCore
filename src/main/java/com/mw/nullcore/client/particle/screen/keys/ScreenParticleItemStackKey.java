package com.mw.nullcore.client.particle.screen.keys;

import net.minecraft.world.item.ItemStack;

public record ScreenParticleItemStackKey(boolean isHotbarItem, boolean isRenderedAfterItem, ItemStack itemStack){
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof ScreenParticleItemStackKey(boolean hotbarItem, boolean renderedAfterItem, ItemStack stack))){
            return false;
        }
        return hotbarItem == isHotbarItem && renderedAfterItem == isRenderedAfterItem && stack == itemStack;
    }

}
