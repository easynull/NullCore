package com.mw.nullcore.core.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface Tickable {
    void tick();
    static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (level, pos, state, be) -> ((Tickable) be).tick();
    }
}
