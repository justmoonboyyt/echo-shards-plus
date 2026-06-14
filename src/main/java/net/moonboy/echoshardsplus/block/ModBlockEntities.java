package net.moonboy.echoshardsplus.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.moonboy.echoshardsplus.EchoShardsPlus;

public class ModBlockEntities {
    public static final BlockEntityType<XPChamberBlockEntity>
    XP_CHAMBER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(EchoShardsPlus.MOD_ID, "xp_chamber"),
                    FabricBlockEntityTypeBuilder.create(
                            XPChamberBlockEntity::new,
                            ModBlocks.XP_CHAMBER
                    ).build());

    public static void registerModBlockEntities() {
        EchoShardsPlus.LOGGER.info("Registering Mod BlockEntities for " + EchoShardsPlus.MOD_ID);
    }
}
