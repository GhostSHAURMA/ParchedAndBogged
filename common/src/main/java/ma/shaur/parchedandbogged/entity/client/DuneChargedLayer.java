package ma.shaur.parchedandbogged.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DuneChargedLayer extends RenderLayer<DuneRenderState, DuneModel>
{
	public static final ModelLayerLocation DUNE_WIND = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "wind"), "main");
	private static final ResourceLocation DUNE_WIND_LOCATION = ResourceLocation.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "textures/entity/dune/dune_wind.png");
	private final DuneModel model;
	
	public DuneChargedLayer(RenderLayerParent<DuneRenderState, DuneModel> renderLayerParent, EntityModelSet entityModelSet)
	{
		super(renderLayerParent);
		model = new DuneModel(entityModelSet.bakeLayer(DUNE_WIND));
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, DuneRenderState entityRenderState, float f, float g)
	{
		if(!entityRenderState.isPowered) return;
		RenderType renderType = RenderType.breezeWind(DUNE_WIND_LOCATION, xOffset(entityRenderState.ageInTicks) % 1.0F, 0.0F);
	    submitNodeCollector.order(1).submitModel(model, entityRenderState, poseStack, renderType, i, OverlayTexture.NO_OVERLAY, -1, null, entityRenderState.outlineColor, null);
	}

	private float xOffset(float f)
	{
		return f * 0.02F;
	}

}
