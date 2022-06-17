package twilightforest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.client.IWeatherRenderHandler;
import twilightforest.client.renderer.TFSkyRenderer;
import twilightforest.client.renderer.TFWeatherRenderer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import twilightforest.init.BiomeKeys;

public class TwilightForestRenderInfo extends DimensionSpecialEffects {

    private ISkyRenderHandler skyRenderer;
    private IWeatherRenderHandler weatherRenderer;

    public TwilightForestRenderInfo(float cloudHeight, boolean placebo, SkyType fogType, boolean brightenLightMap, boolean entityLightingBottomsLit) {
        super(cloudHeight, placebo, fogType, brightenLightMap, entityLightingBottomsLit);
    }

    @Nullable
    @Override
    public float[] getSunriseColor(float daycycle, float partialTicks) { // Fog color
        return null;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) { // For modifying biome fog color with daycycle
        return biomeFogColor.multiply(daylight * 0.94F + 0.06F, (daylight * 0.94F + 0.06F), (daylight * 0.91F + 0.09F));
    }

    @Override
    public boolean isFoggyAt(int x, int y) { // true = nearFog
        //TODO enable if the fog is fixed to smoothly transition. Otherwise the fog nearness just snaps and it's pretty janky tbh
        /*Player player = Minecraft.getInstance().player;

        if (player == null || player.isCreative() || player.isSpectator() || player.position().y > 42)
            return false; // If player is above the dark forest then no need to make it so spooky. The darkwood leaves cover everything as low as y42.

        ResourceKey<Biome> biome = Minecraft.getInstance().player.level.getBiome(new BlockPos(player.position())).unwrapKey().get();

        // FIXME Make the fog on these biomes much much darker, maybe pitch black even. Do we keep this harsher fog underground too?
        return biome == BiomeKeys.DARK_FOREST || biome == BiomeKeys.DARK_FOREST_CENTER;*/
        return false;
    }

    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        if (skyRenderer == null)
            skyRenderer = new TFSkyRenderer();
        return skyRenderer;
    }

    @Nullable
    @Override
    public IWeatherRenderHandler getWeatherRenderHandler() {
        if (weatherRenderer == null)
            weatherRenderer = new TFWeatherRenderer();
        return weatherRenderer;
    }
}
