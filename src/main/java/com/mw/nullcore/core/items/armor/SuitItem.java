package com.mw.nullcore.core.items.armor;

import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SuitItem extends ArmorItem {
    final ArmorMaterial material;
    final ArmorType type;
    final boolean useTooltip;

    public SuitItem(Properties prop, ArmorType type, ArmorMaterial material, boolean useTooltip) {
        super(material, type, prop);
        this.material = material;
        this.type = type;
        this.useTooltip = useTooltip;
    }

    public SuitItem(Properties prop, ArmorType type, ArmorMaterial material) {
        this(prop, type, material, true);
    }

    public static Map<ArmorType, ItemStack> getSet(ArmorMaterial material) {
        Map<ArmorType, ItemStack> set = new EnumMap<>(ArmorType.class);
        for (var item : BuiltInRegistries.ITEM) {
            if (item instanceof SuitItem aItem) {
                if (aItem.getMaterial() == material) {
                    ArmorType type = aItem.getType();
                    set.put(type, new ItemStack(aItem));
                }
            }
        }
        return set;
    }

    public static boolean hasArmorSet(Player player, ArmorMaterial material) {
        for (Map.Entry<ArmorType, ItemStack> entry : getSet(material).entrySet()) {
            ArmorType type = entry.getKey();
            ItemStack actualStack = entry.getValue();
            ItemStack armorStack = player.getInventory().getArmor(type.getSlot().getIndex());

            if (!ItemStack.isSameItemSameComponents(armorStack, actualStack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (!useTooltip) return;
        var effects = ArmorMaterialBuilder.effects.get(getMaterial());
        NcUtils.Text.addTooltipEffects(tooltip, Component.translatable("tooltip.nullcore.suitinfo1").withStyle(ChatFormatting.GRAY), Arrays.stream(effects).toList());
    }

    public ArmorMaterial getMaterial() {
        return material;
    }

    public ArmorType getType() {
        return type;
    }
}
