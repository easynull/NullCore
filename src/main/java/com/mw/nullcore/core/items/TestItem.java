package com.mw.nullcore.core.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw.nullcore.core.entities.AdvancedItemEntity;
import com.mw.nullcore.utils.ColorUtils;
import com.mw.nullcore.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public final class TestItem extends Item implements GuiRender, GuiParticleRender {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        AdvancedItemEntity entity = new AdvancedItemEntity(level, pos, Items.APPLE.getDefaultInstance(), true)
                .setRays(true, 0xDD000000).setParticle(true, ParticleTypes.ASH, 5);
        if (!level.isClientSide) {
            level.addFreshEntity(entity);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("rainbow").withColor(ColorUtils.getRainbowColor()));
    }

    @Override
    public void renderItemGUI(GuiGraphics gg, LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset) {
        PoseStack ps = gg.pose();
        ps.pushPose();
        ps.translate(pX + 8, pY + 9, 100);
        RenderUtils.RenderingBuilder.builder().renderType(RenderType::guiTextured, "nullcore", "textures/particle/light.png")
                .color(ColorUtils.getRainbowColor()).poseStack(ps).renderCenteredQuad(12f);
        ps.popPose();
    }

    @Override
    public void renderParticleGui(LivingEntity entity, Level level, ItemStack stack, int pX, int pY, int seed, int guiOffset) {

    }
}
