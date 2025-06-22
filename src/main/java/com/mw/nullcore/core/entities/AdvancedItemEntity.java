package com.mw.nullcore.core.entities;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AdvancedItemEntity extends ItemEntity {
    private ParticleOptions particle;
    public Pair<Boolean, Integer> raysRender$Color = Pair.of(true, 0xFF00DD00);

    protected AdvancedItemEntity(EntityType<? extends AdvancedItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AdvancedItemEntity(Level level, float pX, float pY, float pZ, ItemStack stack) {
        this(NullEntities.advancedItem.get(), level);
        setPos(pX, pY, pZ);
        setItem(stack);
    }

    public AdvancedItemEntity(Level level, BlockPos pos, ItemStack stack, boolean upBlock) {
        this(level, pos.getX() + 0.5f, pos.getY() + (upBlock ? 1.5f : 0.5f), pos.getZ() + 0.5f, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (particle != null && level().isClientSide && tickCount % 10 == 0) {
            level().addParticle(particle, getX(), getY() + 0.2f, getZ(), 0, 0, 0);
        }
    }

    public AdvancedItemEntity setParticle(ParticleOptions particle) {
        this.particle = particle;
        return this;
    }

    public AdvancedItemEntity setRays(boolean enabled, int color){
        raysRender$Color = Pair.of(enabled, color);
        return this;
    }
}
