package com.mw.nullcore.core.items;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface AmbientItem {
    void entityTick(Level level, Vec3 pos, ItemEntity entity);
}
