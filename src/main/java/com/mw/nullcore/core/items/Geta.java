package com.mw.nullcore.core.items;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.blocks.InteractionBlock;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class Geta extends Block implements ScreenHave, InteractionBlock {
    public Geta(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return actionScreen(level, pos, player);
    }

    @Override
    public Screen getScreen(Level level, Player player) {
        return new ChatScreen("CHOTAAAAA");
    }
}
