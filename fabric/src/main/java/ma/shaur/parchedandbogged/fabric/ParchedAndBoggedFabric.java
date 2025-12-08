package ma.shaur.parchedandbogged.fabric;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import ma.shaur.parchedandbogged.entity.Entities;
import ma.shaur.parchedandbogged.worldgen.BiomeData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;

public final class ParchedAndBoggedFabric implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		ParchedAndBogged.init();
		
		BiomeModifications.create(BiomeData.SPAWNERS_MODIFIER).add(ModificationPhase.REPLACEMENTS, context ->
		{
			return context.getBiomeRegistryEntry().is(Biomes.DESERT) || context.getBiomeRegistryEntry().is(BiomeTags.IS_BADLANDS) || context.getBiomeRegistryEntry().is(ConventionalBiomeTags.IS_DESERT) || context.getBiomeRegistryEntry().is(ConventionalBiomeTags.IS_BADLANDS); 
		}, 
		(context, properties) ->
		{
			properties.getSpawnSettings().removeSpawnsOfEntityType(EntityType.CREEPER);
			properties.getSpawnSettings().addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.CREEPER, 4, 4), 10);
			properties.getSpawnSettings().addSpawn(MobCategory.MONSTER, new SpawnerData(Entities.DUNE.get(), 4, 4), 90);
		});
	}
}
