package com.mw.nullcore.core;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.platform.Platform;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class NullComponents {
    public static final Supplier<DataComponentType<CompoundTag>> nbt = register("nbt", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(CompoundTag.CODEC)));

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Platform.PLATFORM.register(BuiltInRegistries.DATA_COMPONENT_TYPE, name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static void initialization(){
        NullCore.LOG.info("Success initialization components!");
    }
}
