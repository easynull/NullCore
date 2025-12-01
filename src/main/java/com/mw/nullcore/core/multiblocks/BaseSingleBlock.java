package com.mw.nullcore.core.multiblocks;

import com.ibm.icu.impl.Pair;
import com.mw.nullcore.core.entities.ShyItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class BaseSingleBlock implements Blueprint {
    private final ResultEntry required;
    private final Item activator;
    private final Consumer<ShyItemEntity> entity;
    private BiPredicate<Player, ItemStack> condition;
    private Pair<SoundEvent, ParticleOptions> effects;

    public BaseSingleBlock(ResultEntry required, Item activator, Consumer<ShyItemEntity> entity) {
        this.required = required;
        this.activator = activator;
        this.entity = entity;
    }

    public BaseSingleBlock(ResultEntry required, Item activator) {
        this(required, activator, null);
    }

    @Override
    public Direction getDirectionStructure(Level level, BlockPos centerPos) {
        BlockState state = level.getBlockState(centerPos);
        if(state.getBlock() == required.required()) {
            if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
                return state.getValue(HorizontalDirectionalBlock.FACING);
            }
            return Direction.NORTH;
        }
        return null;
    }

    @Override
    public Structure getStructure(Level level, BlockPos pos) {
        Direction direction = getDirectionStructure(level, pos);
        if (direction != null) {
            return new Structure(0, 0, 0, direction);
        }
        return null;
    }

    @Override
    public void onBuilt(Level level, BlockPos startPos, Structure structure) {
        if (structure == null) return;
        applyEffects(level, startPos);
        applyResult(level, startPos, required.result(), structure.facing());
    }

    @Override
    public boolean canActivate(Player player, Level level, ItemStack stack) {
        boolean flag = stack.is(activator);
        return condition != null ? condition.test(player, stack) && flag : flag;
    }

    @Override
    public final BaseSingleBlock condition(BiPredicate<Player, ItemStack> condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public final BaseSingleBlock effects(SoundEvent sound, ParticleOptions particle) {
        effects = Pair.of(sound, particle);
        return this;
    }

    @Override
    public Consumer<ShyItemEntity> getEntity() {
        return entity;
    }

    @Override
    public Item getActivator() {
        return activator;
    }

    @Override
    public Pair<SoundEvent, ParticleOptions> getEffects() {
        return effects;
    }
}
