package twilightforest.structures.courtyard;

import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.structures.StructureTFComponent;

import java.util.Random;

public class ComponentNagaCourtyardIntersection extends StructureTFComponent {
    private static final ResourceLocation HEDGE_CROSS = new ResourceLocation(TwilightForestMod.ID, "courtyard/hedge_intersection_big");
    private static final ResourceLocation HEDGE_CROSS_BIG = new ResourceLocation(TwilightForestMod.ID, "courtyard/hedge_corner_big");

    @SuppressWarnings({"WeakerAccess", "unused"})
    public ComponentNagaCourtyardIntersection() {
        super();
    }

    @SuppressWarnings("WeakerAccess")
    public ComponentNagaCourtyardIntersection(TFFeature feature, int i, int x, int y, int z) {
        super(feature, i);
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 5, y + 5, z + 5);
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        BlockPos pos = new BlockPos(this.getBoundingBox().minX, this.getBoundingBox().minY, this.getBoundingBox().minZ);

        MinecraftServer server = worldIn.getMinecraftServer();
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();

        PlacementSettings placementSettings = new PlacementSettings()
                .setReplacedBlock(Blocks.STRUCTURE_VOID)
                .setBoundingBox(this.getBoundingBox());

        Template template = templateManager.getTemplate(server, HEDGE_CROSS);
        template.addBlocksToWorldChunk(worldIn, pos, placementSettings);

        Template templateBig = templateManager.getTemplate(server, HEDGE_CROSS_BIG);
        templateBig.addBlocksToWorldChunk(worldIn, pos, placementSettings.setIntegrity(ComponentNagaCourtyardMain.HEDGE_INTEGRITY));

        return true;
    }
}
