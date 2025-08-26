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

public class EntitibleBlock extends Block implements EntityBlock {
    final Supplier<BlockEntityType<?>> be;

    public EntitibleBlock(Properties properties, Supplier<BlockEntityType<?>> type) {
        super(properties);
        this.be = type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return be.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (l, pos, s, be) -> {
            if (be instanceof Tickable tickable) {
                tickable.tick();
            }
        };
    }
}
