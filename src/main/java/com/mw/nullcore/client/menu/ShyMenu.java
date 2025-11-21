package com.mw.nullcore.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ShyMenu extends AbstractContainerMenu {
    protected final Container container;
    protected final Inventory playerInv;
    protected final int startIndex;

    protected ShyMenu(MenuType<?> type, int id, Inventory playerInv, Container container) {
        super(type, id);
        this.playerInv = playerInv;
        this.container = container;

        addPlayerHotbar(playerInv);
        addPlayerInventory(playerInv);
        this.startIndex = this.slots.size();

        addContainerSlots();
    }

    protected abstract void addContainerSlots();

    protected Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }

    protected void addSlotRange(Container inv, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(inv, index + i, x + i * dx, y));
        }
    }

    protected void addSlotBox(Container inv, int index, int x, int y, int horAmount, int verAmount, int dx, int dy) {
        for (int j = 0; j < verAmount; j++) {
            addSlotRange(inv, index + j * horAmount, x, y + j * dy, horAmount, dx);
        }
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = 84 + row * 18;
                addSlot(new Slot(inv, col + row * 9 + 9, x, y));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index >= startIndex) {
            if (!moveToPlayerInventory(original, true)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            if (!moveToContainer(original)) {
                if (index < 9) {
                    if (!moveItemStackTo(original, 9, 36, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(original, 0, 9, false)) return ItemStack.EMPTY;
                }
            }
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    protected boolean moveToPlayerInventory(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 0, 36, reverse); // 36 = весь инвентарь игрока
    }

    protected boolean moveToContainer(ItemStack stack) {
        return moveItemStackTo(stack, startIndex, slots.size(), false);
    }

    protected boolean canInsertIntoContainer(ItemStack stack) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int start, int end, boolean reverse) {
        if (start == startIndex && end == slots.size() && !canInsertIntoContainer(stack)) {
            return false;
        }
        return super.moveItemStackTo(stack, start, end, reverse);
    }

    @Override
    public boolean stillValid(Player player) {
        return container == null || container.stillValid(player);
    }
}
