//package com.mw.nullcore.client.render;
//
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormat;
//import net.minecraft.client.renderer.RenderType;
//
//public final class NullRenderTypes extends RenderType{
//    public static final RenderType ADDITIVE_TEXTURE = RenderType.create(
//            "nullcore:additive_texture",
//            DefaultVertexFormat.POSITION_TEX_COLOR,
//            VertexFormat.Mode.QUADS,
//            256,
//            false,
//            true,
//            RenderType.CompositeState.builder().setWriteMaskState(COLOR_WRITE).setLightmapState(NO_LIGHTMAP).setTransparencyState(ADDITIVE_TRANSPARENCY).setTextureState(BLOCK_SHEET).setShaderState(RENDERTYPE_TEXT_SHADER).setCullState(NO_CULL).createCompositeState(true)
//    );
//
//    public NullRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
//        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
//    }
//}
