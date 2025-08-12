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

import java.util.function.Supplier;

public class EasyEntityBlock extends Block implements EntityBlock {
    final Supplier<BlockEntityType<?>> be;

    public EasyEntityBlock(Properties properties, Supplier<BlockEntityType<?>> blockEntityType) {
        super(properties);
        this.be = blockEntityType;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return be.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Tickable.getTicker();
    }

//    public boolean isClientTicker(Level level, BlockState state){
//        return true;
//    }
}
