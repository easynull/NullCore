package com.mw.nullcore.core.blocks.type;

import com.mw.nullcore.core.managers.LockableManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface LockableRegion {
    Map<ResourceLocation, LockableManager.LockableEntry> getLockable(BlockPos pos, BlockState state);
}
