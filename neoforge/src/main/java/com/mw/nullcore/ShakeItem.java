package com.mw.nullcore;

import com.mw.nullcore.core.items.armor.SuitItem;
import net.minecraft.world.item.equipment.ArmorType;

public class ShakeItem extends SuitItem {
    public ShakeItem(Properties prop, ArmorType type) {
        super(prop, type, ArmorMaterials.easynull);
    }
}
