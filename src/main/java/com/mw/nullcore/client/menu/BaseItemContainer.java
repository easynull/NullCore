package com.mw.nullcore.client.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BaseItemContainer extends SimpleContainer {
    private final ItemStack stack;

    public BaseItemContainer(ItemStack stack, int size) {
        super(size);
        this.stack = stack;
        contentMenu();
    }

    public void contentMenu() {
        var contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(getItems());
    }

    @Override
    public boolean stillValid(Player player) {
        return !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ItemContainerContents contents = ItemContainerContents.fromItems(getItems());
        if (contents == ItemContainerContents.EMPTY) stack.remove(DataComponents.CONTAINER);
        stack.set(DataComponents.CONTAINER, contents);
    }
}
