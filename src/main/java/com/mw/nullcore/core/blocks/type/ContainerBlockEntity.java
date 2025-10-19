package com.mw.nullcore.core.blocks.type;

import com.mw.nullcore.core.NcUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ContainerBlockEntity extends BlockEntity implements ContainerHave, Container {
    public final SimpleContainer inventory;
    public final int maxInSlot;

    public ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots, int maxInSlot) {
        super(type, pos, state);
        this.inventory = new SimpleContainer(slots) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerBlockEntity.this.setChanged();
                NcUtils.Block.updateBlockEntity(ContainerBlockEntity.this);
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getFirst() {
        return inventory.getItem(0);
    }

    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = inventory.removeItem(slot, amount);
        setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        setChanged();
    }

    public void setFirst(ItemStack stack) {
        setItem(0, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    @Override
    public void clearContent() {
        inventory.clearContent();
        setChanged();
    }
}
