package twilightforest.client.model.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import twilightforest.entity.monster.MosquitoSwarm;

public class MosquitoSwarmModel extends HierarchicalModel<MosquitoSwarm> {
	private final ModelPart root;
	private final ModelPart core;
	private final ModelPart group1;
	private final ModelPart group2;
	private final ModelPart group3;
	private final ModelPart group4;
	private final ModelPart group5;
	private final ModelPart group6;

	private static final RandomSource rand = RandomSource.create();

	public MosquitoSwarmModel(ModelPart root) {
		this.root = root;

		this.core = this.root.getChild("core");

		this.group1 = this.core.getChild("group_1");
		this.group2 = this.core.getChild("group_2");
		this.group3 = this.core.getChild("group_3");
		this.group4 = this.core.getChild("group_4");
		this.group5 = this.core.getChild("group_5");
		this.group6 = this.core.getChild("group_6");
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		var core = partdefinition.addOrReplaceChild("core", CubeListBuilder.create()
				.texOffs(rand.nextInt(28), rand.nextInt(28))
				.addBox(-0.5F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		PartPose offset = PartPose.offset(-0.5F, -2F, -0.5F);

		for (Direction dir : Direction.values()) {
			addBugsToGroup(dir.ordinal(), core.addOrReplaceChild("group_" + (dir.ordinal() + 1), CubeListBuilder.create()
					.texOffs(rand.nextInt(28), rand.nextInt(28))
					.addBox(dir.getStepX() * 11.0F, dir.getStepY() * 11.0F, dir.getStepZ() * 11.0F, 1.0F, 1.0F, 1.0F),
				offset));
		}

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	/**
	 * Add the proper number of mosquitoes to the groups
	 */
	public static void addBugsToGroup(int iteration, PartDefinition parent) {
		final int bugs = 16;

		for (int i = 0; i < bugs; i++) {
			Vector3f vec = new Vector3f(0.0F, 0.0F, 0.0F);
			float yRot = ((i * (360.0F / bugs)) * Mth.PI) / 180.0F;
			vec.rotateX(yRot);

			float bugX = (rand.nextFloat() - rand.nextFloat()) * 16.0F;
			float bugY = (rand.nextFloat() - rand.nextFloat()) * 16.0F;
			float bugZ = (rand.nextFloat() - rand.nextFloat()) * 16.0F;

			parent.addOrReplaceChild("bug_" + (iteration * bugs + i), CubeListBuilder.create()
					.texOffs(rand.nextInt(28), rand.nextInt(28))
					.addBox(bugX, bugY, bugZ, 1.0F, 1.0F, 1.0F),
				PartPose.offsetAndRotation(vec.x(), vec.y(), vec.z(), 0, yRot, 0));
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(MosquitoSwarm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(MosquitoSwarm entity, float limbSwing, float limbSwingAmount, float partialTicks) {
		this.core.yRot = (entity.tickCount + partialTicks) / 5.0F;
		this.core.xRot = Mth.sin((entity.tickCount + partialTicks) / 5.0F) / 4.0F;
		this.core.zRot = Mth.cos((entity.tickCount + partialTicks) / 5.0F) / 4.0F;

		this.group1.yRot = (entity.tickCount + partialTicks) / 2.0F;
		this.group1.xRot = Mth.sin((entity.tickCount + partialTicks) / 6.0F) / 2.0F;
		this.group1.zRot = Mth.cos((entity.tickCount + partialTicks) / 5.0F) / 4.0F;

		this.group2.yRot = Mth.sin((entity.tickCount + partialTicks) / 2.0F) / 3.0F;
		this.group2.xRot = (entity.tickCount + partialTicks) / 5.0F;
		this.group2.zRot = Mth.cos((entity.tickCount + partialTicks) / 5.0F) / 4.0F;

		this.group3.yRot = Mth.sin((entity.tickCount + partialTicks) / 7.0F) / 3.0F;
		this.group3.xRot = Mth.cos((entity.tickCount + partialTicks) / 4.0F) / 2.0F;
		this.group3.zRot = (entity.tickCount + partialTicks) / 5.0F;

		this.group4.xRot = (entity.tickCount + partialTicks) / 2.0F;
		this.group4.zRot = Mth.sin((entity.tickCount + partialTicks) / 6.0F) / 2.0F;
		this.group4.yRot = Mth.sin((entity.tickCount + partialTicks) / 5.0F) / 4.0F;

		this.group5.zRot = (entity.tickCount + partialTicks) / 2.0F;
		this.group5.yRot = Mth.cos((entity.tickCount + partialTicks) / 5.0F) / 4.0F;
		this.group5.xRot = Mth.cos((entity.tickCount + partialTicks) / 5.0F) / 4.0F;

		this.group6.zRot = Mth.cos((entity.tickCount + partialTicks) / 7.0F) / 3.0F;
		this.group6.xRot = Mth.cos((entity.tickCount + partialTicks) / 4.0F) / 2.0F;
		this.group6.yRot = (entity.tickCount + partialTicks) / 5.0F;
	}
}
