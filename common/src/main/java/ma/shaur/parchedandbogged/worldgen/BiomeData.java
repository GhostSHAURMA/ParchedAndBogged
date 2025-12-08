package ma.shaur.parchedandbogged.worldgen;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeData
{
	public final static ResourceLocation SPAWNERS_MODIFIER = ResourceLocation.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "spawners_modifier");
	public static final TagKey<Biome> TAG_C_IS_BADLANDS = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", "is_badlands"));
}
