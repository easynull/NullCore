package com.mw.nullcore.core;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import static com.mw.nullcore.NullCore.ID;

public final class NullComponents {
    public static final DeferredRegister.DataComponents components = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ID);

    public static final Supplier<DataComponentType<CompoundTag>> nbt = components.registerComponentType("nbt", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(CompoundTag.CODEC)));
    public static final Supplier<DataComponentType<Integer>> color = components.registerComponentType("color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.fromCodec(Codec.INT)));
}
