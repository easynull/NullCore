package com.mw.nullcore.core.blocks;

import com.mw.nullcore.core.blocks.type.TestBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class TestBlock extends BaseEntityBlock implements InteractionBlock {
    public TestBlock(Properties prop) {
        super(prop, TestBE::new);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return actionInventory(state, stack, level, pos, player, hand);
    }
}
