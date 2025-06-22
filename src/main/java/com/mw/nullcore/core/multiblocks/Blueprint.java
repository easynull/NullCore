package com.mw.nullcore.core.multiblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface Blueprint {
    Map<String, Blueprint> registry = new HashMap();

    String getId();

    @Nullable
    Direction validateStructure(Level level, BlockPos centerPos);

    void onBuilt(Level level, BlockPos startPos, Structure structure, ParticleOptions destroyParticle, Object... other);

    Structure getStructure(Level level, BlockPos centerPos);

    default boolean canActivate(Player player, Level level, ItemStack stack){
        return false;
    }

    Blueprint condition(TriPredicate<Player, Level, ItemStack> condition);

    static void register(Blueprint blueprint) {
        if (blueprint != null) {
            registry.put(blueprint.getId(), blueprint);
        }
    }

    static <T extends Blueprint> T get(String id){
        return (T) registry.get(id);
    }

    default void applyResult(Level level, BlockPos pos, Object result, Direction facing) {
        if (result instanceof Block block) {
            BlockState state = block.defaultBlockState();
            if (facing != null && state.hasProperty(HorizontalDirectionalBlock.FACING)) {
                state = state.setValue(HorizontalDirectionalBlock.FACING, facing);
            }
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        } else if (result instanceof Item item) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(item));
            entity.setGlowingTag(true);
            entity.setNoGravity(true);
            entity.setDeltaMovement(0, 0, 0);
            level.addFreshEntity(entity);
        }
    }

    record Structure(int xOffset, int yOffset, int zOffset, Direction facing) {}
    record Result(Object required, Object result) {}

    class Rotation {
        int rows;
        int cols;
        Object[][] matrix;

        public Rotation(Object[][] matrix) {
            rows = matrix.length;
            cols = matrix[0].length;
            this.matrix = new Object[rows][cols];
            for (int i = 0; i < rows; ++i) {
                System.arraycopy(matrix[i], 0, this.matrix[i], 0, cols);
            }
        }

        public void rotateRight(int times) {
            for (int a = 0; a < times; ++a) {
                Object[][] newMatrix = new Object[cols][rows];
                for (int i = 0; i < rows; ++i) {
                    for (int j = 0; j < cols; ++j) {
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
