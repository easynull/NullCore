package com.mw.nullcore.registers;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mw.nullcore.NullCore.ID;

public final class NcComponents {
    public static final DeferredRegister.DataComponents components = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ID);

    public static final Supplier<DataComponentType<CompoundTag>> NBT = components.registerComponentType("nbt", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(CompoundTag.CODEC)));
    public static final Supplier<DataComponentType<Boolean>> ENABLED = components.registerComponentType("enabled", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final Supplier<DataComponentType<BlockState>> BLOCK = components.registerComponentType("block", builder -> builder.persistent(BlockState.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(BlockState.CODEC)));
    public static final Supplier<DataComponentType<BlockPos>> POS = components.registerComponentType("pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
}
