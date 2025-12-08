package ma.shaur.parchedandbogged.neoforge;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import ma.shaur.parchedandbogged.entity.Entities;
import ma.shaur.parchedandbogged.entity.client.DuneChargedLayer;
import ma.shaur.parchedandbogged.entity.client.DuneModel;
import ma.shaur.parchedandbogged.entity.client.DuneRenderer;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(ParchedAndBogged.MOD_ID)
public final class ParchedAndBoggedForge
{	
	public ParchedAndBoggedForge()
	{
		ParchedAndBogged.init();
	}
	
	@EventBusSubscriber(modid = ParchedAndBogged.MOD_ID, value = Dist.CLIENT)
	public static class ClientEvents
	{
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event)
		{
			EntityRenderers.register(Entities.DUNE.get(), DuneRenderer::new);
		}
		
		@SubscribeEvent
		public static void registerLayersEvent(EntityRenderersEvent.RegisterLayerDefinitions event)
		{
			event.registerLayerDefinition(DuneModel.DUNE, () -> DuneModel.createBodyLayer(new CubeDeformation(0.0F)));
			event.registerLayerDefinition(DuneChargedLayer.DUNE_WIND, () -> DuneModel.createBodyLayer(new CubeDeformation(2.0F)));
		}
	}
}
