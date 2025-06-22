package com.mw.nullcore.core.items;

import com.mw.nullcore.core.entities.AdvancedItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            AdvancedItemEntity entity = new AdvancedItemEntity(level, pos, Items.APPLE.getDefaultInstance(), true).setRays(true, 0xFF000000).setParticle(ParticleTypes.EXPLOSION);
            level.addFreshEntity(entity);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
