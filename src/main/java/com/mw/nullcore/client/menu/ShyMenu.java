package com.mw.nullcore.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ShyMenu extends AbstractContainerMenu {
    protected final Container container;
    protected final Inventory inv;
    protected final int inventoryX;
    protected final int inventoryY;
    protected final int index;
    final boolean withHotbar;
    public final Player player;

    protected ShyMenu(MenuType<?> type, int id, Inventory inv, Container container, int inventoryX, int inventoryY, boolean withHotbar) {
        super(type, id);
        this.inv = inv;
        this.container = container;
        this.inventoryX = inventoryX;
        this.inventoryY = inventoryY;
        this.withHotbar = withHotbar;
        this.player = inv.player;

        if (withHotbar) addPlayerHotbar(inv);
        addPlayerInventory(inv);
        this.index = this.slots.size();
    }

    protected void addSlotRange(Container cont, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(cont, index + i, x + i * dx, y));
        }
    }

    protected void addSlotBox(Container cont, int index, int x, int y, int horAmount, int verAmount, int dx, int dy) {
        for (int j = 0; j < verAmount; j++) {
            addSlotRange(cont, index + j * horAmount, x, y + j * dy, horAmount, dx);
        }
    }

    private void addPlayerInventory(Inventory inv) {
        addSlotBox(inv, 9, inventoryX, inventoryY, 9, 3, 18, 18);
    }

    private void addPlayerHotbar(Inventory inv) {
        addSlotRange(inv, 0, inventoryX, inventoryY + 58, 9, 18);
    }

    public int getContainerSlotCount() {
        return slots.size() - index;
    }

    public Slot getSlotAt(int index) {
        return index >= 0 && index < slots.size() ? slots.get(index) : null;
    }

    public Slot getContainerSlot(int containerIndex) {
        int absoluteIndex = index + containerIndex;
        return absoluteIndex < slots.size() ? slots.get(absoluteIndex) : null;
    }

    public List<Slot> getContainerSlots() {
        return slots.subList(index, slots.size());
    }

    public List<Slot> getPlayerSlots() {
        return slots.subList(0, index);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index >= this.index) {
            if (!moveToPlayerInventory(original, true)) {
                return ItemStack.EMPTY;
            }
        } else {
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

        slot.onTake(player, original);
        return copy;
    }

    protected boolean moveToPlayerInventory(ItemStack stack, boolean reverse) {
        return moveItemStackTo(stack, 0, 36, reverse);
    }

    protected boolean moveToContainer(ItemStack stack) {
        return moveItemStackTo(stack, index, slots.size(), false);
    }

    protected boolean canInsertIntoContainer(ItemStack stack) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int start, int end, boolean reverse) {
        if (start == index && end == slots.size() && !canInsertIntoContainer(stack)) {
            return false;
        }
        return super.moveItemStackTo(stack, start, end, reverse);
    }

    public void clearContainer() {
        for (int i = index; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.hasItem()) {
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    public boolean isEmpty() {
        for (int i = index; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.hasItem()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return container == null || container.stillValid(player);
    }

    public Container getContainer() {
        return container;
    }

    public Inventory getInventory() {
        return inv;
    }
}