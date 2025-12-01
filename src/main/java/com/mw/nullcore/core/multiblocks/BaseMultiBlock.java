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
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class BaseMultiBlock implements Blueprint {
    public final Object[][][] structure;
    final int xSize, ySize, zSize;
    final Item activator;
    private final Consumer<ShyItemEntity> entity;
    private BiPredicate<Player, ItemStack> condition;
    private Pair<SoundEvent, ParticleOptions> effects;

    public BaseMultiBlock(Object[][][] structure, Item activator, Consumer<ShyItemEntity> entity) {
        this.structure = structure;
        this.ySize = structure.length;
        this.xSize = structure[0].length;
        this.zSize = structure[0][0].length;
        this.activator = activator;
        this.entity = entity;
    }

    public BaseMultiBlock(Object[][][] structure, Item activator) {
       this(structure, activator, null);
    }

    @Override
    public Direction getDirectionStructure(Level level, BlockPos centerPos) {
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
                        if (element instanceof ResultEntry result) {
                            value = result.required();
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
        for (int yy =  -ySize; yy <= 0; ++yy) {
            for (int xx = -xSize; xx <= 0; ++xx) {
                for (int zz = -zSize; zz <= 0; ++zz) {
                    BlockPos p2 = pos.offset(xx, yy, zz);
                    Direction d = getDirectionStructure(level, p2);
                    if (d != null) {
                        return new Structure(xx, yy, zz, d);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onBuilt(Level level, BlockPos startPos, Structure structure) {
        if (structure == null) return;
        BlockPos p2 = startPos.offset(structure.xOffset(), structure.yOffset(), structure.zOffset());
        for (int y = 0; y < ySize; ++y) {
            Rotation rot = new Rotation(this.structure[y]);
            rot.rotateRight(3 - structure.facing().get2DDataValue());
            for (int x = 0; x < rot.rows; ++x) {
                for (int z = 0; z < rot.cols; ++z) {
                    BlockPos p3 = p2.offset(x, -y + (ySize - 1), z);
                    if (rot.matrix[x][z] != null) applyEffects(level, p3);
                    if (rot.matrix[x][z] instanceof ResultEntry result) {
                        applyResult(level, p3, result.result(), structure.facing());
                    }
                }
            }
        }
    }

    @Override
    public boolean canActivate(Player player, Level level, ItemStack stack) {
        boolean flag = stack.is(activator);
        return condition != null ? condition.test(player, stack) && flag : flag;
    }

    @Override
    public final BaseMultiBlock condition(BiPredicate<Player, ItemStack> condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public final BaseMultiBlock effects(SoundEvent sound, ParticleOptions particle) {
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
