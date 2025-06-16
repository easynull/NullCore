package com.mw.nullcore.core.multiblocks;

import com.mw.nullcore.core.items.BlueprintActivator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;
import org.jetbrains.annotations.Nullable;

public class BaseMultiBlock implements Blueprint {
    final Object[][][] structure;
    final String id;
    final int xSize;
    final int ySize;
    final int zSize;
    final Item activator;
    TriPredicate<Player, Level, ItemStack> condition = null;

    public BaseMultiBlock(String id, Object[][][] structure, Item activator) {
        this.id = id;
        this.structure = structure;
        ySize = structure.length;
        xSize = structure[0].length;
        zSize = structure[0][0].length;
        this.activator = activator;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public @Nullable Direction validateStructure(Level level, BlockPos centerPos) {
        Direction[] horizontals = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        for (Direction face : horizontals) {
            boolean matches = true;
            for (int y = 0; y < ySize; y++) {
                Rotation rotation = new Rotation(structure[y]);
                rotation.rotateRight(3 - face.get2DDataValue());
                for (int x = 0; x < rotation.rows; x++) {
                    for (int z = 0; z < rotation.cols; z++) {
                        Object element = rotation.matrix[x][z];
                        if (element == null) continue;
                        BlockPos checkPos = centerPos.offset(x, -y + (ySize - 1), z);
                        BlockState worldState = level.getBlockState(checkPos);
                        ItemLike value = null;
                        if (element instanceof Result result) {
                            if (result.required() instanceof ItemLike l) {
                                value = l;
                            }
                        } else if (element instanceof ItemLike l) {
                            value = l;
                        }
                        if (value != null) {
                            Block requiredBlock = Block.byItem(value.asItem());
                            if (requiredBlock != Blocks.AIR && !worldState.is(requiredBlock)) {
                                matches = false;
                            }
                        }
                    }
                }
            }
            if (matches) {
                return face;
            }
        }
        return null;
    }

    @Override
    public Structure getStructure(Level level, BlockPos pos) {
        for (byte yy = (byte) -ySize; yy <= 0; ++yy) {
            for (byte xx = (byte) -xSize; xx <= 0; ++xx) {
                for (byte zz = (byte) -zSize; zz <= 0; ++zz) {
                    BlockPos p2 = pos.offset(xx, yy, zz);
                    Direction d = validateStructure(level, p2);
                    if (d != null) {
                        return new Structure(xx, yy, zz, d);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onBuilt(Level level, BlockPos startPos, Structure structure, ParticleOptions destroyParticle) {
        if (structure == null) return;
        BlockPos p2 = startPos.offset(structure.xOffset(), structure.yOffset(), structure.zOffset());
        for (byte y = 0; y < ySize; ++y) {
            Rotation bRot = new Rotation(this.structure[y]);
            bRot.rotateRight(3 - structure.facing().get2DDataValue());
            for (byte x = 0; x < bRot.rows; ++x) {
                for (byte z = 0; z < bRot.cols; ++z) {
                    BlockPos p3 = p2.offset(x, -y + (ySize - 1), z);
                    if (destroyParticle != null) level.addParticle(destroyParticle, p3.getX() + 0.5f, p3.getY() + 0.5f, p3.getZ() + 0.5f, 1f, 1f, 1f);
                    else level.destroyBlock(p3, false);
                    if (bRot.matrix[x][z] instanceof Result result) {
                        applyResult(level, p3, result.result(), structure.facing());
                    }
                }
            }
        }
    }

    @Override
    public boolean canActivate(Player player, Level level, ItemStack stack) {
        boolean flag = stack.getItem() instanceof BlueprintActivator && stack.is(activator);
        return condition != null ? condition.test(player, level, stack) && flag : flag;
    }

    @Override
    public Blueprint condition(TriPredicate<Player, Level, ItemStack> condition){
        this.condition = condition;
        return this;
    }
}
