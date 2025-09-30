package com.mw.nullcore.registers;

import com.mojang.serialization.Codec;
import com.mw.nullcore.NullCore;
import com.mw.nullcore.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class NullComponents {
    public static final Supplier<DataComponentType<CompoundTag>> nbt = register("nbt", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(CompoundTag.CODEC)));
    public static Supplier<DataComponentType<Boolean>> active = register("active", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static Supplier<DataComponentType<BlockState>> blockState = register("block_state", builder -> builder.persistent(BlockState.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(BlockState.CODEC)));
    public static Supplier<DataComponentType<BlockPos>> pos = register("block_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Platform.PLATFORM.register(BuiltInRegistries.DATA_COMPONENT_TYPE, name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static void initialization(){
        NullCore.LOG.info("Success initialization components!");
    }
}
