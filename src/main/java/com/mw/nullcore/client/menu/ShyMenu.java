package com.mw.nullcore.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ShyMenu extends AbstractContainerMenu {
    protected final int startIndex;
    protected final Container container;

    protected ShyMenu(@Nullable MenuType<?> menuType, int id, Container container) {
        super(menuType, id);
        this.container = container;
        this.startIndex = this.slots.size();
        addPlayerInventory(container);
        addMenuSlots(container);
    }

    protected abstract void addMenuSlots(Container container);

    private void addPlayerInventory(Container playerInv) {
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
        return container.stillValid(player);
    }
}
