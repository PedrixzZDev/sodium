package net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.impl;

import net.caffeinemc.mods.sodium.client.gl.attribute.GlVertexAttributeFormat;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexFormatAttribute;

public class DefaultChunkMeshAttributes {
    public static final VertexFormatAttribute POSITION = new VertexFormatAttribute("POSITION", GlVertexAttributeFormat.UNSIGNED_INT, 2, false, true);
    public static final VertexFormatAttribute COLOR = new VertexFormatAttribute("COLOR", GlVertexAttributeFormat.UNSIGNED_BYTE, 4, true, false);
    public static final VertexFormatAttribute TEXTURE_COORD = new VertexFormatAttribute("TEXTURE_COORD", GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false, false);
    public static final VertexFormatAttribute TEXTURE_ORIGIN = new VertexFormatAttribute("TEXTURE_ORIGIN", GlVertexAttributeFormat.UNSIGNED_SHORT, 2, false, false);
    public static final VertexFormatAttribute LIGHT_INDEX = new VertexFormatAttribute("LIGHT_MATERIAL_INDEX", GlVertexAttributeFormat.UNSIGNED_BYTE, 4, false, true);
}
