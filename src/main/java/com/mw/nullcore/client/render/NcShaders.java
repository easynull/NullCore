package com.mw.nullcore.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mw.nullcore.NullCore;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NcShaders {
    public static final ShaderProgram SCREENPARTICLE = new ShaderProgram(NullCore.path("screen_particle"), DefaultVertexFormat.POSITION_TEX_COLOR, ShaderDefines.builder().define("screen_particle").build());
}
