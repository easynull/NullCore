package com.mw.nullcore.core.blocks;

import com.mw.nullcore.core.blocks.type.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseEntityBlock extends Block implements EntityBlock {
    final BlockEntityType.BlockEntitySupplier be;

    public BaseEntityBlock(Properties prop, BlockEntityType.BlockEntitySupplier be) {
        super(prop);
        this.be = be;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return be.create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return be == type ? Tickable.getTicker() : null;
    }
}
