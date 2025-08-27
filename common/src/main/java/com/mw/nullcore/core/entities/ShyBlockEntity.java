package com.mw.nullcore.core.entities;

import com.mw.nullcore.registers.NullEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ShyBlockEntity extends Entity {
    static final EntityDataAccessor<BlockState> block = SynchedEntityData.defineId(ShyBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    static final EntityDataAccessor<Integer> lifeTime = SynchedEntityData.defineId(ShyBlockEntity.class, EntityDataSerializers.INT);
    static Type anim = Type.fall;

    public ShyBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ShyBlockEntity(Level level, double pX, double pY, double pZ, BlockState state) {
        super(NullEntities.shyBlock.get(), level);
        setPos(pX, pY, pZ);
        setBlock(state);
    }

    public ShyBlockEntity(Level level, BlockPos pos, BlockState state) {
        this(level, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, state);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(block, Blocks.GRASS_BLOCK.defaultBlockState());
        builder.define(lifeTime, 999);
    }

    @Override
    public void tick() {
        int time = getLifeTime();
        if (time != 999 || getAnimType() == Type.fall) {
            time--;
            setLifeTime(time);
            if (time <= 0) {
                discard();
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damage, float pTick) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setBlock(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK), tag));
        setLifeTime(tag.getInt("life_time"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("block", NbtUtils.writeBlockState(getBlock()));
        tag.putInt("life_time", getLifeTime());
    }

    public ShyBlockEntity setBlock(BlockState state) {
        entityData.set(block, state);
        return this;
    }

    public BlockState getBlock() {
        return entityData.get(block);
    }

    public ShyBlockEntity setLifeTime(int time) {
        entityData.set(lifeTime, time);
        return this;
    }

    public int getLifeTime() {
        return entityData.get(lifeTime);
    }

    public void spawn() {
        level().addFreshEntity(this);
    }

    public Type getAnimType() {
        return anim;
    }

    public ShyBlockEntity setAnimType(Type anim) {
        ShyBlockEntity.anim = anim;
        return this;
    }

    public enum Type {
        fall(-10f),
        mixing(-0.2f),
        rebound(-10f);
        public final float yOffset;

        Type(float yOffset) {
            this.yOffset = yOffset;
        }
    }
}
