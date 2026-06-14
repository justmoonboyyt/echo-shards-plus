package net.moonboy.echoshardsplus;

import net.fabricmc.api.ModInitializer;

import net.moonboy.echoshardsplus.block.ModBlockEntities;
import net.moonboy.echoshardsplus.block.ModBlocks;
import net.moonboy.echoshardsplus.item.ModItemGroups;
import net.moonboy.echoshardsplus.potion.ModPotions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoShardsPlus implements ModInitializer {
	public static final String MOD_ID = "echo_shards_plus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItemGroups.registerItemGroups();
		ModPotions.registerModPotions();
		ModPotions.registerPotionsRecipes();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
	}
}