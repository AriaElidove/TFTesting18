package twilightforest.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import twilightforest.init.TFItems;
import twilightforest.init.TFParticleType;
import twilightforest.init.TFSounds;

public class SnowGuardian extends BaseIceMob {

	public SnowGuardian(EntityType<? extends SnowGuardian> type, Level world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.23D)
				.add(Attributes.ATTACK_DAMAGE, 3.0D)
				.add(Attributes.MAX_HEALTH, 10.0D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TFSounds.SNOW_GUARDIAN_AMBIENT.value();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return TFSounds.SNOW_GUARDIAN_HURT.value();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TFSounds.SNOW_GUARDIAN_DEATH.value();
	}

	@Override
	public float getVoicePitch() {
		return (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 0.8F;
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		int type = random.nextInt(4);
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.makeItemForSlot(EquipmentSlot.MAINHAND, type)));
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(this.makeItemForSlot(EquipmentSlot.CHEST, type)));
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.makeItemForSlot(EquipmentSlot.HEAD, type)));
	}

	private Item makeItemForSlot(EquipmentSlot slot, int type) {
		return switch (slot) {
			default -> switch (type) {
				default -> TFItems.IRONWOOD_SWORD.value();
				case 1 -> TFItems.STEELEAF_SWORD.value();
				case 2, 3 -> TFItems.KNIGHTMETAL_SWORD.value();
			};
			case FEET -> switch (type) {
				default -> TFItems.IRONWOOD_BOOTS.value();
				case 1 -> TFItems.STEELEAF_BOOTS.value();
				case 2 -> TFItems.KNIGHTMETAL_BOOTS.value();
				case 3 -> TFItems.ARCTIC_BOOTS.value();
			};
			case LEGS -> switch (type) {
				default -> TFItems.IRONWOOD_LEGGINGS.value();
				case 1 -> TFItems.STEELEAF_LEGGINGS.value();
				case 2 -> TFItems.KNIGHTMETAL_LEGGINGS.value();
				case 3 -> TFItems.ARCTIC_LEGGINGS.value();
			};
			case CHEST -> switch (type) {
				default -> TFItems.IRONWOOD_CHESTPLATE.value();
				case 1 -> TFItems.STEELEAF_CHESTPLATE.value();
				case 2 -> TFItems.KNIGHTMETAL_CHESTPLATE.value();
				case 3 -> TFItems.ARCTIC_CHESTPLATE.value();
			};
			case HEAD -> switch (type) {
				default -> TFItems.IRONWOOD_HELMET.value();
				case 1 -> TFItems.STEELEAF_HELMET.value();
				case 2 -> TFItems.KNIGHTMETAL_HELMET.value();
				case 3 -> TFItems.ARCTIC_HELMET.value();
			};
		};
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		SpawnGroupData data = super.finalizeSpawn(accessor, difficulty, reason, spawnDataIn, dataTag);
		this.populateDefaultEquipmentSlots(accessor.getRandom(), difficulty);
		this.populateDefaultEquipmentEnchantments(accessor.getRandom(), difficulty);
		return data;
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.level().isClientSide()) {
			for (int i = 0; i < 3; i++) {
				float px = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.3F;
				float py = this.getEyeHeight() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5F;
				float pz = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.3F;

				this.level().addParticle(TFParticleType.SNOW_GUARDIAN.value(), this.xOld + px, this.yOld + py, this.zOld + pz, 0, 0, 0);
			}
		}
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 2;
	}
}
