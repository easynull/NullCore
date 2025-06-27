package com.mw.nullcore.core.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mw.nullcore.client.render.vfx.VFXBuilder;
import com.mw.nullcore.core.entities.AdvancedItemEntity;
import com.mw.nullcore.utils.ClientUtils;
import com.mw.nullcore.utils.ColorUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public final class TestItem extends Item implements GuiRenderer, CreativeTabItem {
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
    public void renderGuiVFX(GuiGraphics gg, Level level, ItemStack stack, int pX, int pY, float pTick) {
        PoseStack ps = gg.pose();
        ps.pushPose();
        float rotationTime = (ClientUtils.clientTick + pTick) * 0.02f + 0.5f;
        float pulse = (Mth.sin((ClientUtils.clientTick + pTick) * 0.4f) * 0.5f + 0.5f);
        float scale = 1f + (1f - 1.3f) * pulse;
        VFXBuilder.create(ps).move(pX + 8f, pY + 8f, 100f).spin(rotationTime).scale(scale).renderType(RenderType::guiTextured, "nullcore", "textures/particle/star.png").color(0xFFDD0000).transparency(1f - scale + 0.5f).build(17f);
        ps.popPose();
    }

    @Override
    public void addCreativeTab(ResourceKey<CreativeModeTab> tab, Collection<ItemStack> output) {
        if(tab == CreativeModeTabs.BUILDING_BLOCKS) output.add(this.getDefaultInstance());
    }
}
