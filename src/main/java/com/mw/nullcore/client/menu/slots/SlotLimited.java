package com.mw.nullcore.client.menu.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public final class SlotLimited extends Slot {
    final Object limiter;
    final Predicate<ItemStack> condition;

    public SlotLimited(Container container, int slot, int x, int y, Class<?> limiter, Predicate<ItemStack> condition) {
        super(container, slot, x, y);
        this.limiter = limiter;
        this.condition = condition;
    }

    public SlotLimited(Container container, int slot, int x, int y, ItemStack limiter, Predicate<ItemStack> condition) {
        super(container, slot, x, y);
        this.limiter = limiter;
        this.condition = condition;
    }

    public SlotLimited(Container container, int slot, int x, int y, Class<?> limiter) {
        this(container, slot, x, y, limiter, null);
    }

    public SlotLimited(Container container, int slot, int x, int y, ItemStack limiter) {
        this(container, slot, x, y, limiter, null);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if(limiter instanceof Class<?> cl){
            return condition == null ? cl.isInstance(stack.getItem()) : cl.isInstance(stack.getItem()) && condition.test(stack);
        } else {
            return condition == null ? ItemStack.isSameItem((ItemStack) limiter, stack) : ItemStack.isSameItem((ItemStack) limiter, stack) && condition.test(stack);
        }
    }
}
