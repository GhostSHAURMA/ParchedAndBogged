package ma.shaur.parchedandbogged.entity.client;

import ma.shaur.parchedandbogged.ParchedAndBogged;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DuneModel extends EntityModel<DuneRenderState>
{
	public static final ModelLayerLocation DUNE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ParchedAndBogged.MOD_ID, "dune"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public DuneModel(ModelPart root) 
	{
		super(root);
		body = root.getChild("body");
		head = body.getChild("head");
	    leftHindLeg = body.getChild("right_hind_leg");
	    rightHindLeg = body.getChild("left_hind_leg");
	    leftFrontLeg = body.getChild("right_front_leg");
	    rightFrontLeg = body.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation)
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -18.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 24.0F, 0.0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, -18.0F, 0.0F));
	    CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation);
	    body.addOrReplaceChild("right_hind_leg", cubeListBuilder, PartPose.offset(-2.0F, -6.0F, 4.0F));
	    body.addOrReplaceChild("left_hind_leg", cubeListBuilder, PartPose.offset(2.0F, -6.0F, 4.0F));
	    body.addOrReplaceChild("right_front_leg", cubeListBuilder, PartPose.offset(-2.0F, -6.0F, -4.0F));
	    body.addOrReplaceChild("left_front_leg", cubeListBuilder, PartPose.offset(2.0F, -6.0F, -4.0F));

		return LayerDefinition.create(meshDefinition, 64, 32);
	}
	
	@Override
	public void setupAnim(DuneRenderState duneRenderState)
	{
		super.setupAnim(duneRenderState);
		head.yRot = duneRenderState.yRot * 0.017453292F;
		head.xRot = duneRenderState.xRot * 0.017453292F;
		float f = duneRenderState.walkAnimationSpeed;
		float g = duneRenderState.walkAnimationPos;
		rightHindLeg.xRot = Mth.cos(g * 0.6662F) * 1.4F * f;
		leftHindLeg.xRot = Mth.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
		rightFrontLeg.xRot = Mth.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
		leftFrontLeg.xRot = Mth.cos(g * 0.6662F) * 1.4F * f;
	}
}
