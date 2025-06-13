package com.mw.nullcore;

import com.mw.nullcore.utils.BlockUtils;
import com.mw.nullcore.utils.TextUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            BlockUtils.forEachCube(pos, 1, (p)-> level.destroyBlock(p, false));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        TextUtils.addTooltipWithKey(GLFW.GLFW_KEY_LEFT_CONTROL, 0xFFFFFFF, list, Component.literal(TextUtils.formatNum(10000000)));
    }
}
