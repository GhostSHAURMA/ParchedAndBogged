package ma.shaur.parchedandbogged.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import ma.shaur.parchedandbogged.entity.Dune;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class DuneRenderer extends MobRenderer<Dune, DuneRenderState, DuneModel>
{
	private static final Identifier DUNE_LOCATION = Identifier.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "textures/entity/dune/dune.png");
	private static final Identifier DUNE_RED_LOCATION = Identifier.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "textures/entity/dune/dune_red.png");
	
	public DuneRenderer(Context context)
	{
		super(context, new DuneModel(context.bakeLayer(DuneModel.DUNE)), 0.5F);
		addLayer(new DuneChargedLayer(this, context.getModelSet()));
	}

	protected void scale(DuneRenderState duneRenderState, PoseStack poseStack)
	{
		float f = duneRenderState.swelling;
		float g = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
		f = Mth.clamp(f, 0.0F, 1.0F);
		f *= f;
		f *= f;
		float h = (1.0F + f * 0.4F) * g;
		float i = (1.0F + f * 0.1F) / g;
		poseStack.scale(h, i, h);
	}

	protected float getWhiteOverlayProgress(DuneRenderState duneRenderState)
	{
		float f = duneRenderState.swelling;
		return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
	}

	public Identifier getTextureLocation(DuneRenderState duneRenderState)
	{
		return duneRenderState.isRed ? DUNE_RED_LOCATION : DUNE_LOCATION;
	}

	public DuneRenderState createRenderState()
	{
		return new DuneRenderState();
	}

	public void extractRenderState(Dune dune, DuneRenderState duneRenderState, float f)
	{
		super.extractRenderState(dune, duneRenderState, f);
		duneRenderState.swelling = dune.getSwelling(f);
		duneRenderState.isPowered = dune.isPowered();
		duneRenderState.isRed = dune.isRed();
	}
}
