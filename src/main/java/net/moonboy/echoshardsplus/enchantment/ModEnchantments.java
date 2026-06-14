package net.moonboy.echoshardsplus.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.moonboy.echoshardsplus.EchoShardsPlus;

public class ModEnchantments {

    public static Enchantment WARDENSHOT = registerEnchantment("wardenshot",
            new WardenshotEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.CROSSBOW, EquipmentSlot.MAINHAND));

    private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(EchoShardsPlus.MOD_ID, name), enchantment);
    }

    public static void registerModEnchantments(){
        EchoShardsPlus.LOGGER.info("Registering Enchantments for " + EchoShardsPlus.MOD_ID);
    }
}
