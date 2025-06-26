package com.mw.nullcore.core.blocks.type;

import com.mw.nullcore.NullCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TestBE extends InventoryBlockEntity {
    int are = 100;
    public TestBE(BlockPos pos, BlockState blockState) {
        super(NullCore.null3.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("are", are);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        are = nbt.getInt("are");
    }
}
