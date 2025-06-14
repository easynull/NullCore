package com.mw.nullcore.core.blocks;

import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class InteractionBlock extends BaseEntityBlock {

    public InteractionBlock(Properties prop, BlockEntityType.BlockEntitySupplier be) {
        super(prop, be);
    }
}
