package net.moonboy.echoshardsplus.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.moonboy.echoshardsplus.EchoShardsPlus;
import net.moonboy.echoshardsplus.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup ECHO_SHARD_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(EchoShardsPlus.MOD_ID, "echo_shard"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.echo_shard"))
                    .icon(() -> new ItemStack(ModBlocks.XP_CHAMBER)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.XP_CHAMBER);
                    }).build());


    public static void registerItemGroups() {
        EchoShardsPlus.LOGGER.info("Registering Item Groups for " + EchoShardsPlus.MOD_ID);
    }
}