package com.mw.nullcore.core.components;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mw.nullcore.NullCores.ID;

public final class NullComponents {
    public static final DeferredRegister.DataComponents components = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ID);

    public static Supplier<DataComponentType<CompoundTag>> nbt = components.registerComponentType("nbt", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(CompoundTag.CODEC)));
}
