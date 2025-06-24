package com.mw.nullcore.core.entities;

import com.mojang.datafixers.util.Pair;
import com.mw.nullcore.core.NullEntities;
import com.mw.nullcore.utils.ClientUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class AdvancedItemEntity extends ItemEntity {
    static final EntityDataAccessor<Boolean> canRays = SynchedEntityData.defineId(AdvancedItemEntity.class, EntityDataSerializers.BOOLEAN), canParticle = SynchedEntityData.defineId(AdvancedItemEntity.class, EntityDataSerializers.BOOLEAN);
    static final EntityDataAccessor<Integer> color = SynchedEntityData.defineId(AdvancedItemEntity.class, EntityDataSerializers.INT), countParticle = SynchedEntityData.defineId(AdvancedItemEntity.class, EntityDataSerializers.INT);
    static final EntityDataAccessor<ParticleOptions> particle = SynchedEntityData.defineId(AdvancedItemEntity.class, EntityDataSerializers.PARTICLE);

    public AdvancedItemEntity(EntityType<AdvancedItemEntity> entityType, Level level) {
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
        if (level().isClientSide) {
            if (entityData.get(canParticle) && tickCount % 15 == 0) {
                ClientUtils.forParticleSpawn(level(), getParticle(), (float) getX(), (float) getY() + 0.3f, (float) getZ(), entityData.get(countParticle));
            }
        }
    }

    public AdvancedItemEntity setParticle(boolean enabled, ParticleOptions particle, int count) {
        entityData.set(canParticle, enabled);
        entityData.set(AdvancedItemEntity.particle, particle != null ? particle : ParticleTypes.EFFECT);
        entityData.set(countParticle, count);
        return this;
    }

    public AdvancedItemEntity setRays(boolean enabled, int color) {
        entityData.set(canRays, enabled);
        entityData.set(AdvancedItemEntity.color, color);
        return this;
    }

    public Pair<Boolean, Integer> getRays() {
        return Pair.of(entityData.get(canRays), entityData.get(color));
    }

    public ParticleOptions getParticle() {
        return entityData.get(particle);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(canRays, false);
        builder.define(color, 0);
        builder.define(canParticle, false);
        builder.define(particle, ParticleTypes.EFFECT);
        builder.define(countParticle, 0);
    }
}
