package twilightforest.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import twilightforest.block.CinderFurnaceBlock;
import twilightforest.init.TFBlocks;

public class CinderFurnaceBlockEntity extends FurnaceBlockEntity {
	private static final int SMELT_LOG_FACTOR = 10;

	public CinderFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	// [VanillaCopy] of superclass, edits noted
	public static void tick(Level level, BlockPos pos, BlockState state, CinderFurnaceBlockEntity te) {
		boolean flag = te.isBurning();
		boolean flag1 = false;

		if (te.isBurning()) {
			--te.litTime;
		}

		if (!level.isClientSide()) {
			ItemStack itemstack = te.items.get(1);

			if (te.isBurning() || !itemstack.isEmpty() && !te.items.get(0).isEmpty()) {
				RecipeHolder<?> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(te.items.getFirst()), level).orElse(null);
				if (recipe != null && !te.isBurning() && te.canBurn(level, recipe.value())) {
					te.litTime = te.getBurnDuration(itemstack);
					te.litDuration = te.litTime;

					if (te.isBurning()) {
						flag1 = true;

						if (!itemstack.isEmpty()) {
							Item item = itemstack.getItem();
							itemstack.shrink(1);

							if (itemstack.isEmpty()) {
								ItemStack item1 = item.getCraftingRemainingItem(itemstack);
								te.items.set(1, item1);
							}
						}
					}
				}

				if (recipe != null && te.isBurning() && te.canBurn(level, recipe.value())) {
					// TF - cook faster
					te.cookingProgress += te.getCurrentSpeedMultiplier(level);

					if (te.cookingProgress >= te.cookingTotalTime) { // TF - change to geq since we can increment by >1
						te.cookingProgress = 0;
						te.cookingTotalTime = te.getRecipeBurnTime(level);
						te.smeltItem(level, recipe.value());
						flag1 = true;
					}
				} else {
					te.cookingProgress = 0;
				}
			} else if (!te.isBurning() && te.cookingProgress > 0) {
				te.cookingProgress = Mth.clamp(te.cookingProgress - 2, 0, te.cookingTotalTime);
			}

			if (flag != te.isBurning()) {
				flag1 = true;
				level.setBlock(pos, level.getBlockState(pos).setValue(CinderFurnaceBlock.LIT, te.isBurning()), 3); // TF - use our furnace
			}

			// TF - occasionally cinderize nearby logs
			if (te.isBurning() && te.litTime % 5 == 0) {
				te.cinderizeNearbyLog(level, pos);
			}
		}

		if (flag1) {
			te.setChanged();
		}
	}

	// [VanillaCopy] of super
	private boolean isBurning() {
		return this.litTime > 0;
	}

	// [VanillaCopy] of super, only using SMELTING IRecipeType
	protected int getRecipeBurnTime(Level level) {
		return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(this.items.getFirst()), level).map(recipeHolder -> recipeHolder.value().getCookingTime()).orElse(200);
	}

	@SuppressWarnings("deprecation")
	private void cinderizeNearbyLog(Level level, BlockPos origin) {
		RandomSource rand = level.getRandom();

		int dx = rand.nextInt(2) - rand.nextInt(2);
		int dy = rand.nextInt(2) - rand.nextInt(2);
		int dz = rand.nextInt(2) - rand.nextInt(2);
		BlockPos pos = origin.offset(dx, dy, dz);

		if (level.hasChunkAt(pos)) {
			BlockState nearbyBlock = level.getBlockState(pos);

			if (!nearbyBlock.is(TFBlocks.CINDER_LOG) && nearbyBlock.is(BlockTags.LOGS)) {
				level.setBlock(pos, this.getCinderLog(dx, dy, dz), 2);
				level.levelEvent(2004, pos, 0);
				level.levelEvent(2004, pos, 0);
				level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	/**
	 * What meta should we set the log block with the specified offset to?
	 */
	private BlockState getCinderLog(int dx, int dy, int dz) {
		@Nullable Direction.Axis direction;
		if (dz == 0 && dx != 0) {
			direction = dy == 0 ? Direction.Axis.X : Direction.Axis.Z;
		} else if (dx == 0 && dz != 0) {
			direction = dy == 0 ? Direction.Axis.Z : Direction.Axis.X;
		} else if (dx == 0) {
			direction = Direction.Axis.Y;
		} else {
			direction = dy == 0 ? Direction.Axis.Y : null; //We return null so we can get Cinder Wood.
		}

		return direction != null ? TFBlocks.CINDER_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction)
			: TFBlocks.CINDER_WOOD.get().defaultBlockState();
	}

	/**
	 * What is the current speed multiplier, as an int.
	 */
	private int getCurrentSpeedMultiplier(Level level) {
		return this.getCurrentMultiplier(level, 2);
	}

	/**
	 * Returns a number that is based on the number of nearby logs divided by the factor given.
	 */
	private int getCurrentMultiplier(Level level, int factor) {
		int logs = this.countNearbyLogs(level);

		if (logs < factor) {
			return 1;
		} else {
			return (logs / factor) + (level.getRandom().nextInt(factor) >= (logs % factor) ? 0 : 1);
		}
	}

	@SuppressWarnings("deprecation")
	private int countNearbyLogs(Level level) {
		int count = 0;

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					BlockPos pos = getBlockPos().offset(dx, dy, dz);
					if (level.hasChunkAt(pos) && level.getBlockState(pos).is(TFBlocks.CINDER_LOG.get())) {
						count++;
					}
				}
			}
		}

		return count;
	}

	// [VanillaCopy] of superclass ver, changes noted
	//@Override
	protected boolean canBurn(Level level, Recipe<?> recipe) {
		if (this.items.get(0).isEmpty()) {
			return false;
		} else {
			ItemStack itemstack = recipe.getResultItem(level.registryAccess());

			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.items.get(2);
				if (itemstack1.isEmpty()) return true;
				if (!itemstack1.is(itemstack.getItem())) return false;
				int result = itemstack1.getCount() + getMaxOutputStacks(level, this.items.getFirst(), itemstack); // TF - account for multiplying
				return result <= this.getMaxStackSize() && result <= itemstack1.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
			}
		}
	}

	/**
	 * Return the max number of items in the output stack, given our current multiplier
	 */
	public int getMaxOutputStacks(Level level, ItemStack input, ItemStack output) {
		if (this.canMultiply(input)) {
			return output.getCount() * this.getCurrentMaxSmeltMultiplier(level);
		} else {
			return output.getCount();
		}
	}

	// [VanillaCopy] superclass, using our own canSmelt and multiplying output
	public void smeltItem(Level level, Recipe<?> recipe) {
		if (this.canBurn(level, recipe)) {
			ItemStack itemstack = this.items.getFirst();
			ItemStack itemstack1 = recipe.getResultItem(level.registryAccess());
			itemstack1.setCount(itemstack1.getCount() * this.getCurrentSmeltMultiplier(level));
			ItemStack itemstack2 = this.items.get(2);

			if (itemstack2.isEmpty()) {
				this.items.set(2, itemstack1.copy());
			} else if (itemstack2.getItem() == itemstack1.getItem()) {
				itemstack2.grow(itemstack1.getCount());
			}

			if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
				this.items.set(1, new ItemStack(Items.WATER_BUCKET));
			}

			itemstack.shrink(1);
		}
	}

	private boolean canMultiply(ItemStack input) {
		return input.is(ItemTags.LOGS) || input.is(Tags.Items.ORES);
	}

	/**
	 * What is the current speed multiplier, as an int.
	 */
	private int getCurrentSmeltMultiplier(Level level) {
		return this.getCurrentMultiplier(level, SMELT_LOG_FACTOR);
	}

	/**
	 * What is the current speed multiplier, as an int.
	 */
	private int getCurrentMaxSmeltMultiplier(Level level) {
		return (int) Math.ceil((float) this.countNearbyLogs(level) / (float) SMELT_LOG_FACTOR);
	}
}
