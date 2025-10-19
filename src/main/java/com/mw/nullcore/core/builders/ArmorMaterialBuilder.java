package com.mw.nullcore.core.builders;

import com.mw.nullcore.NullCore;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class ArmorMaterialBuilder {
    public static final Map<ArmorMaterial, ShyEffect[]> effects = new HashMap<>();
    int durabilityMultiplier = 1;
    int[] protectionAmounts = new int[]{2, 5, 6, 2, 5};
    int enchantability = 10;
    private Holder<SoundEvent> sound = SoundEvents.ARMOR_EQUIP_IRON;
    float toughness;
    float knockbackResistance;
    private TagKey<Item> repair;
    private ResourceKey<EquipmentAsset> asset;
    private ShyEffect[] shyEffects;

    public static ArmorMaterialBuilder builder() {
        return new ArmorMaterialBuilder();
    }

    public ArmorMaterialBuilder durability(int multiplier) {
        this.durabilityMultiplier = multiplier;
        return this;
    }

    public ArmorMaterialBuilder protection(int boots, int leggings, int chestplate, int helmet, int fullSet) {
        this.protectionAmounts = new int[]{boots, leggings, chestplate, helmet, fullSet};
        return this;
    }

    public ArmorMaterialBuilder protection(int[] protectionArray) {
        this.protectionAmounts = protectionArray;
        return this;
    }

    public ArmorMaterialBuilder enchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public ArmorMaterialBuilder sound(Holder<SoundEvent> sound) {
        this.sound = sound;
        return this;
    }

    public ArmorMaterialBuilder toughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public ArmorMaterialBuilder resistance(float resistance) {
        this.knockbackResistance = resistance;
        return this;
    }

    public ArmorMaterialBuilder repairTag(TagKey<Item> repair) {
        this.repair = repair;
        return this;
    }

    public ArmorMaterialBuilder asset(String modid, String id) {
        final ResourceKey<Registry<EquipmentAsset>> equipmentAssets = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(modid, "equipment_asset"));
        this.asset = NullCore.key(equipmentAssets, modid, id);
        return this;
    }

    public ArmorMaterialBuilder asset(ResourceKey<EquipmentAsset> asset) {
        this.asset = asset;
        return this;
    }

    public ArmorMaterialBuilder effects(ShyEffect... effects) {
        this.shyEffects = effects;
        return this;
    }

    public ArmorMaterial build() {
        Map<ArmorType, Integer> defenseMap = new EnumMap<>(ArmorType.class);
        defenseMap.put(ArmorType.BOOTS, protectionAmounts[0]);
        defenseMap.put(ArmorType.LEGGINGS, protectionAmounts[1]);
        defenseMap.put(ArmorType.CHESTPLATE, protectionAmounts[2]);
        defenseMap.put(ArmorType.HELMET, protectionAmounts[3]);
        defenseMap.put(ArmorType.BODY, protectionAmounts[4]);

        ArmorMaterial material = new ArmorMaterial(durabilityMultiplier * 10, Map.copyOf(defenseMap), enchantability, sound, toughness, knockbackResistance, repair, asset);
        if (shyEffects != null) effects.put(material, shyEffects);
        return material;
    }

    public static ShyEffect of(Holder<MobEffect> effect, int time, int level, boolean visible, Predicate<Player> condition){
        return new ShyEffect(effect, time, level, visible, condition);
    }

    public static ShyEffect of(Holder<MobEffect> effect, int time, int level, boolean visible){
        return of(effect, time, level, visible, p -> true);
    }

    public static ShyEffect of(Holder<MobEffect> effect, int time, int level){
        return of(effect, time, level, true);
    }

    public static ShyEffect of(Holder<MobEffect> effect, int time){
        return of(effect, time, 0);
    }

    public record ShyEffect(Holder<MobEffect> effect, int time, int level, boolean visible, Predicate<Player> condition) {}
}
