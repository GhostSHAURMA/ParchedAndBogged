package ma.shaur.parchedandbogged.entity;

import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import ma.shaur.parchedandbogged.ParchedAndBogged;
import ma.shaur.parchedandbogged.entity.client.DuneChargedLayer;
import ma.shaur.parchedandbogged.entity.client.DuneModel;
import ma.shaur.parchedandbogged.entity.client.DuneRenderer;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class Entities
{
	private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ParchedAndBogged.MOD_ID, Registries.ENTITY_TYPE);
	public static final RegistrySupplier<EntityType<Dune>> DUNE = register(resourceLocation("dune"), EntityType.Builder.of(Dune::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).notInPeaceful());
	
	public static void register()
	{
		ENTITIES.register();
		
		EntityAttributeRegistry.register(DUNE, Dune::createAttributes);
		SpawnPlacementsRegistry.register(DUNE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
	}

	private static <T extends Entity> RegistrySupplier<EntityType<T>> register(ResourceLocation resourceLocation, EntityType.Builder<T> builder)
	{
		return ENTITIES.register(resourceLocation, () -> builder.build(ResourceKey.create(Registries.ENTITY_TYPE, resourceLocation)));
	}

	private static ResourceLocation resourceLocation(String name)
	{
		return ResourceLocation.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, name);
	}

	public static void initEntityRendering()
	{
		if(Platform.isNeoForge()) return;
		EntityRendererRegistry.register(DUNE, DuneRenderer::new);
		EntityModelLayerRegistry.register(DuneModel.DUNE, () -> DuneModel.createBodyLayer(new CubeDeformation(0.0F)));
		EntityModelLayerRegistry.register(DuneChargedLayer.DUNE_WIND, () -> DuneModel.createBodyLayer(new CubeDeformation(2.0F)));
	}

}
