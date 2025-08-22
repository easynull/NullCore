package com.mw.nullcore.core.items.armor;

import com.mojang.serialization.Codec;
import com.mw.nullcore.Utils;
import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.*;

import static com.mw.nullcore.core.items.armor.SuitItem.getSet;

public class CompactSuitItem extends Item {
    final ArmorMaterial material;

    public CompactSuitItem(Properties prop, ArmorMaterial material) {
        super(prop.stacksTo(1).durability(material.durability() * getSet(material).size()).enchantable(material.enchantmentValue()));
        this.material = material;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (equipArmorSet(player)) {
            ItemStack stack = player.getItemInHand(hand);
            player.getCooldowns().addCooldown(stack, 20);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var effects = ArmorMaterialBuilder.effects.get(getMaterial());
        Utils.Text.addTooltipEffects(tooltip, Component.translatable("tooltip.nullcore.suitinfo2").withStyle(ChatFormatting.GRAY), Arrays.stream(effects).toList());
    }

    private boolean equipArmorSet(Player player) {
        Inventory inv = player.getInventory();
        for (ArmorType type : ArmorType.values()) {
            ItemStack armor = inv.getArmor(type.getSlot().getIndex());
            if (!armor.isEmpty()) {
                return false;
            }
        }
        for (Map.Entry<ArmorType, ItemStack> entry : getSet(getMaterial()).entrySet()) {
            ArmorType type = entry.getKey();
            ItemStack armorStack = entry.getValue().copy();
            inv.armor.set(type.getSlot().getIndex(), armorStack);
        }
        return true;
    }

    public static ItemStack returnCompact(Player player, ItemStack armorStack, ClickType click) {
        ArmorMaterial material = getArmorMaterial(armorStack);
        if (material != null) {
            CompactSuitItem compact = findCompact(material);
            if (compact != null ) {
                ItemStack comp = new ItemStack(compact);
                Inventory inv = player.getInventory();
                if (click != ClickType.CLONE) {
                    for (ArmorType type : ArmorType.values()) {
                        inv.armor.set(type.getSlot().getIndex(), ItemStack.EMPTY);
                    }
                }
                return comp;
            }
        }
        return ItemStack.EMPTY;
    }

    private static CompactSuitItem findCompact(ArmorMaterial material) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof CompactSuitItem compact && compact.getMaterial() == material) {
                return compact;
            }
        }
        return null;
    }

    private static ArmorMaterial getArmorMaterial(ItemStack stack) {
        if (stack.getItem() instanceof SuitItem armor) {
            return armor.getMaterial();
        }
        return null;
    }

    public ArmorMaterial getMaterial() {
        return material;
    }
}
