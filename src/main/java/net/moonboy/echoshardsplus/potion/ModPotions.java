package net.moonboy.echoshardsplus.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.moonboy.echoshardsplus.EchoShardsPlus;

public class ModPotions {

    private static Potion registerPotion(String name, Potion potion){
        return Registry.register(Registries.POTION, new Identifier(EchoShardsPlus.MOD_ID, name), potion);
    }

    public static final Potion DARKNESS_POTION = registerPotion("darkness_potion", new Potion(new StatusEffectInstance(StatusEffects.DARKNESS, 3600, 0)));
    public static final Potion EXTENDED_DARKNESS_POTION = registerPotion("extended_darkness_potion", new Potion(new StatusEffectInstance(StatusEffects.DARKNESS, 9600, 0)));

    public static void registerModPotions(){
        EchoShardsPlus.LOGGER.info("Registering Mod Potions for " + EchoShardsPlus.MOD_ID);
    }

    public static void registerPotionsRecipes(){
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.ECHO_SHARD, ModPotions.DARKNESS_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(ModPotions.DARKNESS_POTION, Items.REDSTONE, ModPotions.EXTENDED_DARKNESS_POTION);
    }
}