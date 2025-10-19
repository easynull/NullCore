package com.mw.nullcore.core.blocks.type;

import net.minecraft.world.SimpleContainer;

public interface ContainerHave {
    SimpleContainer getInventory();

    default boolean useMixinSetting(){
        return true;
    }
}
