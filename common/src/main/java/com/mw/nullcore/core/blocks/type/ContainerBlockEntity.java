package com.mw.nullcore.core.blocks.type;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.mw.nullcore.Utils;
import net.minecraft.world.entity.player.Player;

public abstract class ContainerBlockEntity extends BlockEntity implements ContainerHave {
    public final SimpleContainer inventory;
    public final int maxInSlot;

    public ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots, int maxInSlot) {
        super(type, pos, state);
        this.inventory = new SimpleContainer(slots) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerBlockEntity.this.setChanged();
                Utils.Block.updateBlockEntity(ContainerBlockEntity.this);
            }
        };
        this.maxInSlot = maxInSlot;
    }

    public ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots) {
        this(type, pos, state, slots, 1);
    }

    public ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 1, 1);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            items.set(i, inventory.getItem(i));
        }
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    public ItemStack getFirst() {
        return inventory.getItem(0).copy();
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public int getSize() {
        return inventory.getContainerSize();
    }

    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = inventory.removeItem(slot, amount);
        setChanged();
        return stack;
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        setChanged();
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public void clear() {
        inventory.clearContent();
        setChanged();
    }
}
