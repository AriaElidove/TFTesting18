package twilightforest.client.renderer.tileentity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import twilightforest.TwilightForestMod;
import twilightforest.block.entity.TwilightChestEntity;
import twilightforest.init.TFBlocks;

import java.util.EnumMap;
import java.util.Map;

public class TwilightChestRenderer<T extends TwilightChestEntity> extends ChestRenderer<T> {
    public static final Map<Block, EnumMap<ChestType, Material>> MATERIALS;

    static {
        ImmutableMap.Builder<Block, EnumMap<ChestType, Material>> builder = ImmutableMap.builder();

        builder.put(TFBlocks.TWILIGHT_OAK_CHEST.value(), chestMaterial("twilight"));
        builder.put(TFBlocks.CANOPY_CHEST.value(), chestMaterial("canopy"));
        builder.put(TFBlocks.MANGROVE_CHEST.value(), chestMaterial("mangrove"));
        builder.put(TFBlocks.DARK_CHEST.value(), chestMaterial("darkwood"));
        builder.put(TFBlocks.TIME_CHEST.value(), chestMaterial("time"));
        builder.put(TFBlocks.TRANSFORMATION_CHEST.value(), chestMaterial("trans"));
        builder.put(TFBlocks.MINING_CHEST.value(), chestMaterial("mining"));
        builder.put(TFBlocks.SORTING_CHEST.value(), chestMaterial("sort"));

        MATERIALS = builder.build();
    }

    public TwilightChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(T blockEntity, ChestType chestType) {
        EnumMap<ChestType, Material> b = MATERIALS.get(blockEntity.getBlockState().getBlock());

        if (b == null) return super.getMaterial(blockEntity, chestType);

        Material material = b.get(chestType);

        return material != null ? material : super.getMaterial(blockEntity, chestType);
    }

    private static EnumMap<ChestType, Material> chestMaterial(String type) {
        EnumMap<ChestType, Material> map = new EnumMap<>(ChestType.class);

        map.put(ChestType.SINGLE, new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/" + type)));
        map.put(ChestType.LEFT, new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/left")));
        map.put(ChestType.RIGHT, new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/right")));

        return map;
    }
}
