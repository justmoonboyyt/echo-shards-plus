package net.moonboy.echoshardsplus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EchoShardsPlusClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(Items.CROSSBOW,
                new Identifier(EchoShardsPlus.MOD_ID, "echo_loaded"),
                (stack, world, entity, seed) -> {
                    EchoShardsPlus.LOGGER.info("Predicate firing, charged:" + CrossbowItem.isCharged(stack));
                    EchoShardsPlus.LOGGER.info("NBT:" + stack.getNbt());
                    if (!CrossbowItem.isCharged(stack)) return 0.0f;
                    NbtCompound nbt = stack.getNbt();
                    if (nbt == null) return 0.0f;
                    if (!nbt.contains("ChargedProjectiles")) return 0.0f;
                    NbtList projectiles = nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
                    for (int i = 0; i < projectiles.size(); i++) {
                        ItemStack projectile = ItemStack.fromNbt(projectiles.getCompound(i));
                        EchoShardsPlus.LOGGER.info("ChargedProjectile:" + projectile.getItem());
                        if (projectile.isOf(Items.ECHO_SHARD)) return 1.0f;
                    }
                    return 0.0f;
                }
        );
    }
}
