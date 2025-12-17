package ma.shaur.parchedandbogged.worldgen;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeData
{
	public final static Identifier SPAWNERS_MODIFIER = Identifier.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "spawners_modifier");
	public static final TagKey<Biome> TAG_C_IS_BADLANDS = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "is_badlands"));
}
