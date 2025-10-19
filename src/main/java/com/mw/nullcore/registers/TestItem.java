//package com.mw.nullcore.registers;
//
//import com.mw.nullcore.client.particle.screen.ScreenParticleHolder;
//import com.mw.nullcore.client.render.NcRenderType;
//import com.mw.nullcore.core.builders.ScreenParticleBuilder;
//import com.mw.nullcore.core.entities.ShyBlockEntity;
//import com.mw.nullcore.core.items.Renderable;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.block.Blocks;
//
//public final class TestItem extends Item implements Renderable {
//    public TestItem(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        ShyBlockEntity entity = new ShyBlockEntity(context.getLevel(), context.getClickedPos().above(), Blocks.ACACIA_PLANKS.defaultBlockState());
//        entity.spawn();
//        return super.useOn(context);
//    }
//
//    @Override
//    public boolean isParticleRenderable() {
//        return true;
//    }
//
//    @Override
//    public void renderLate(ScreenParticleHolder target, GuiGraphics gg, ClientLevel level, float pTick, ItemStack stack, int x, int y) {
//        ScreenParticleBuilder.create(NcParticles.WISP, target).setRenderType(NcRenderType.ADDITIVE).spawnOnStack(0, 0);
//    }
//}
