package twilightforest.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import twilightforest.TwilightForestMod;
import twilightforest.world.*;
import twilightforest.worldgen.biomes.BiomeMaker;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class TwilightWorldDataCompiler extends WorldDataCompilerAndOps<JsonElement> {
	public TwilightWorldDataCompiler(DataGenerator generator) {
		super(generator, JsonOps.INSTANCE, GSON::toJson, new DynamicRegistries.Impl());
	}

	@Override
	public void generate(DirectoryCache directoryCache) {
		getDimensions().forEach((rl, dimension) -> serialize(Registry.DIMENSION_KEY, rl, dimension, Dimension.CODEC));
		getBiomes().forEach((rl, biome) -> serialize(Registry.BIOME_KEY, rl, biome, Biome.CODEC));
	}

	private Map<ResourceLocation, Dimension> getDimensions() {
		NoiseSettings forestNoiseSettings = new NoiseSettings(
				128, // Noise Height - This allows us to shorten the world so we can cram more stuff upwards
				new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
				new SlideSettings(-10, 3, 0),
				new SlideSettings(-30, 0, 0),
				1,
				2,
				1.0D,
				-0.46875D,
				false,
				true,
				false,
				false
		);

		Optional<DimensionSettings> forestDimensionSettings = makeDimensionSettings(
				new DimensionStructuresSettings(Optional.empty(), ImmutableMap.of()),
				forestNoiseSettings,
				Blocks.STONE.getDefaultState(),
				Blocks.WATER.getDefaultState(),
				-10,
				0,
				31,
				false
		);

		// https://misode.github.io/worldgen/noise-settings/
		// So far this looks great! We just need to raise the island levels to sea level. Otherwise is generates flat-flakey islands that really show the roots on dirt bottoms from trees
		NoiseSettings skyNoiseSettings = new NoiseSettings(
				128,
				new ScalingSettings(3.0D, 1.0D, 256.0D, 16.0D),
				new SlideSettings(-3000, 92, -66),
				new SlideSettings(-30, 7, 1),
				2,
				1,
				0.3D,
				-0.2D,
				false,
				true,
				false,
				false
		);

		Optional<DimensionSettings> skyDimensionSettings = makeDimensionSettings(
				new DimensionStructuresSettings(Optional.empty(), ImmutableMap.of()),
				skyNoiseSettings,
				Blocks.STONE.getDefaultState(),
				Blocks.WATER.getDefaultState(),
				-10,
				-10,
				0,
				false
		);

		// Register the dimension noise settings in the local datagen registry. Hacky.
		getOrCreateInRegistry(dynamicRegistries.getRegistry(Registry.NOISE_SETTINGS_KEY), RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, TwilightForestMod.prefix("forest_noise_config")), forestDimensionSettings::get);
		getOrCreateInRegistry(dynamicRegistries.getRegistry(Registry.NOISE_SETTINGS_KEY), RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, TwilightForestMod.prefix("sky_noise_config")), skyDimensionSettings::get);

		TFDimensions.init();
		ChunkGeneratorTwilightBase forestChunkGen = new ChunkGeneratorTwilightForest(new TFBiomeProvider(0L, new SimpleRegistry<>(Registry.BIOME_KEY, Lifecycle.experimental())), 0L, forestDimensionSettings::get);
		ChunkGeneratorTwilightBase skyChunkGen = new ChunkGeneratorTwilightForest(new TFBiomeProvider(0L, new SimpleRegistry<>(Registry.BIOME_KEY, Lifecycle.experimental())), 0L, skyDimensionSettings::get);

		Optional<DimensionType> twilightType = makeDimensionType(
				OptionalLong.of(13000L), // TODO Kill the celestial bodies
				true,
				false,
				false,
				true,
				0.125D,
				false,
				false,
				true,
				true,
				false,
				256,
				FuzzedBiomeMagnifier.INSTANCE,
				new ResourceLocation("infiniburn_overworld"),
				TwilightForestMod.prefix("renderer"), // DimensionRenderInfo
				0f // Wish this could be set to -0.05 since it'll make the world truly blacked out if an area is not sky-lit (see: Dark Forests) Sadly this also messes up night vision so it gets 0
		);

		// Register the type in the local datagen registry. Hacky.
		getOrCreateInRegistry(dynamicRegistries.getRegistry(Registry.DIMENSION_TYPE_KEY), RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation(TwilightForestMod.ID, "forest_type")), twilightType::get);

		return ImmutableMap.of(
				// TODO add underscore to twilightforest
				TwilightForestMod.prefix("twilightforest"), new Dimension(twilightType::get, forestChunkGen),
				TwilightForestMod.prefix("skylight_forest"), new Dimension(twilightType::get, skyChunkGen)
				// TODO add *actual* twilightforest:void world without islands
		);
	}

	private Map<ResourceLocation, Biome> getBiomes() {
		return BiomeMaker
				.generateBiomes()
				.entrySet()
				.stream()
				.peek(registryKeyBiomeEntry -> registryKeyBiomeEntry.getValue().setRegistryName(registryKeyBiomeEntry.getKey().getLocation()))
				.collect(Collectors.toMap(entry -> entry.getKey().getLocation(), Map.Entry::getValue));
	}
}
