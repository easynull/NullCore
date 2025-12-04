package com.mw.nullcore.core.multiblocks;

import com.ibm.icu.impl.Pair;
import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.entities.ShyItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface Blueprint {
    Direction getDirectionStructure(Level level, BlockPos centerPos);

    void onBuilt(Level level, BlockPos startPos, Structure structure);

    Structure getStructure(Level level, BlockPos centerPos);

    default boolean canActivate(Player player, Level level, ItemStack stack) {
        return false;
    }

    Blueprint condition(BiPredicate<Player, ItemStack> condition);

    default void applyResult(Level level, BlockPos pos, Object result, Direction facing) {
        if (result instanceof Block block) {
            BlockState state = block.defaultBlockState();
            if (facing != null && state.hasProperty(HorizontalDirectionalBlock.FACING)) {
                state = state.setValue(HorizontalDirectionalBlock.FACING, facing);
            }
            level.setBlock(pos, state, 2);
        } else if (result instanceof Item item) {
            ItemStack stack = new ItemStack(item);
            ShyItemEntity entity = new ShyItemEntity(level, (float) (pos.getX() + 0.5), (float) (pos.getY() + 0.2), (float) (pos.getZ() + 0.5), stack);
            getEntity().accept(entity);
            entity.setNoGravity(true);
            entity.setDeltaMovement(0, 0, 0);
            level.addFreshEntity(entity);
        }
    }

    default void applyEffects(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            if (getEffects() != null && getEffects().first != null) {
                Minecraft.getInstance().player.playSound(getEffects().first);
            }
            if (getEffects() != null && getEffects().second != null) {
                NcUtils.Particle.forAxisParticle(pos, getEffects().second);
            } else {
                level.addDestroyBlockEffect(pos, level.getBlockState(pos));
            }
        }
        level.removeBlock(pos, false);
    }

    default Consumer<ShyItemEntity> getEntity() {
        return null;
    }

    default Item getActivator() {
        return null;
    }

    default Pair<SoundEvent, ParticleOptions> getEffects() {
        return null;
    }

    Blueprint effects(SoundEvent sound, ParticleOptions particle);

    record Structure(int xOffset, int yOffset, int zOffset, Direction facing) {}

    record ResultEntry(ItemLike required, ItemLike result) {}

    class Rotation {
        int rows, cols;
        Object[][] matrix;

        Rotation(Object[][] matrix) {
            this.rows = matrix.length;
            this.cols = matrix[0].length;
            this.matrix = new Object[rows][cols];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(matrix[i], 0, this.matrix[i], 0, cols);
            }
        }

        void rotateRight(int times) {
            for (int a = 0; a < times; a++) {
                Object[][] newMatrix = new Object[cols][rows];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        newMatrix[j][rows - i - 1] = matrix[i][j];
                    }
                }
                matrix = newMatrix;
                int tmp = rows;
                rows = cols;
                cols = tmp;
            }
        }
    }
}