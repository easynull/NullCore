package com.mw.nullcore;

import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ArmorMaterials {
    public static final ArmorMaterial easynull = ArmorMaterialBuilder.builder().durability(1200).toughness(3.0F).resistance(0.1F).effects(ArmorMaterialBuilder.of(MobEffects.HEALTH_BOOST, Utils.Mth.minuteTick(1), 3), ArmorMaterialBuilder.of(MobEffects.LUCK, Utils.Mth.dayTick(1), 2, false)).repairTag(ItemTags.REPAIRS_NETHERITE_ARMOR).asset(EquipmentAssets.NETHERITE).build();
}
