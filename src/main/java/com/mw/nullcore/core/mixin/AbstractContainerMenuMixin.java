package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.items.armor.CompactSuitItem;
import com.mw.nullcore.core.items.armor.SuitItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public final class AbstractContainerMenuMixin {
    @Inject(method = "doClick", at = @At("HEAD"))
    private void nc$onClick(int slotId, int button, ClickType click, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (slotId < 0 || (!player.isCreative() && player.level().isClientSide)) return;
        Slot slot = menu.slots.get(slotId);
        if (slot instanceof ArmorSlot && slot.hasItem() && slot.getItem().getItem() instanceof SuitItem suit) {
            ItemStack compactStack = CompactSuitItem.returnCompact(player, slot.getItem(), click);
            if (compactStack.getItem() instanceof CompactSuitItem compact && compact.getMaterial() == suit.getMaterial()) {
                compactStack.setDamageValue(5);
                menu.setCarried(compactStack);
            }
        }
    }
}
