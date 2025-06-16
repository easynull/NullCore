package com.mw.nullcore.core.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class NullMenu extends AbstractContainerMenu {
    protected final int startIndex;
    protected final Inventory playerInv;

    protected NullMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInv) {
        super(menuType, containerId);
        this.playerInv = playerInv;
        this.startIndex = this.slots.size();
        addPlayerInventory(playerInv);
        addMenuSlots(playerInv);
    }

    protected abstract void addMenuSlots(Inventory inv);

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            stack = itemstack1.copy();
            if (index < this.startIndex) {
                if (!this.moveItemStackTo(itemstack1, this.startIndex, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, this.startIndex, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return playerInv.stillValid(player);
    }
}
