package com.mw.nullcore.core.entities;

import com.mojang.datafixers.util.Pair;
import com.mw.nullcore.Utils;
import com.mw.nullcore.registers.NullEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class ShyItemEntity extends ItemEntity {
    static final EntityDataAccessor<Boolean> canRays = SynchedEntityData.defineId(ShyItemEntity.class, EntityDataSerializers.BOOLEAN), canParticle = SynchedEntityData.defineId(ShyItemEntity.class, EntityDataSerializers.BOOLEAN);
    static final EntityDataAccessor<Integer> color = SynchedEntityData.defineId(ShyItemEntity.class, EntityDataSerializers.INT), countParticle = SynchedEntityData.defineId(ShyItemEntity.class, EntityDataSerializers.INT);
    static final EntityDataAccessor<ParticleOptions> particle = SynchedEntityData.defineId(ShyItemEntity.class, EntityDataSerializers.PARTICLE);

    public ShyItemEntity(EntityType<ShyItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ShyItemEntity(Level level, float pX, float pY, float pZ, ItemStack stack) {
        this(NullEntities.shyItem.get(), level);
        setPos(pX, pY, pZ);
        setItem(stack);
    }

    public ShyItemEntity(Level level, BlockPos pos, ItemStack stack, boolean upBlock) {
        this(level, pos.getX() + 0.5f, pos.getY() + (upBlock ? 1.5f : 0.5f), pos.getZ() + 0.5f, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (entityData.get(canParticle) && tickCount % 15 == 0) {
                Utils.Particle.forParticleSpawn(level(), getParticle(), (float) getX(), (float) getY() + 0.3f, (float) getZ(), entityData.get(countParticle));
            }
        }
    }

    public ShyItemEntity setParticle(boolean enabled, ParticleOptions particle, int count) {
        entityData.set(canParticle, enabled);
        entityData.set(ShyItemEntity.particle, particle != null ? particle : ParticleTypes.EFFECT);
        entityData.set(countParticle, count);
        return this;
    }

    public ShyItemEntity setRays(boolean enabled, int color) {
        entityData.set(canRays, enabled);
        entityData.set(ShyItemEntity.color, color);
        return this;
    }

    public Pair<Boolean, Integer> getRays() {
        return Pair.of(entityData.get(canRays), entityData.get(color));
    }

    public ParticleOptions getParticle() {
        return entityData.get(particle);
    }

    public void spawn(){
        level().addFreshEntity(this);
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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("canRays", getRays().getFirst());
        compound.putInt("color", getRays().getSecond());
        compound.putBoolean("canParticle", entityData.get(canParticle));
        compound.putInt("countParticle", entityData.get(countParticle));
        Utils.Particle.writeParticle(compound, getParticle());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setRays(compound.getBoolean("canRays"), compound.getInt("color"));
        setParticle(compound.getBoolean("canParticle"), Utils.Particle.readParticle(compound), compound.getInt("countParticle"));
    }
}
