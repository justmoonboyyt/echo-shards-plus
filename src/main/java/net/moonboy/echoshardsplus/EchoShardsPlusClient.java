package net.moonboy.echoshardsplus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class EchoShardsPlusClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(Items.CROSSBOW,
                new Identifier("echo_shards_plus", "echo_loaded"),
                ((stack, world, entity, seed) -> {
                    if (!CrossbowItem.isCharged(stack))
                        return 0.0F;
                    NbtCompound nbt = stack.getNbt();
                    if (nbt==null)
                        return 0.0F;
                    if (!nbt.contains("ChargedProjectiles"))
                        return 0.0F;

                    NbtList projectiles = nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);

                    for (int i = 0; i < projectiles.size(); i++) {
                        ItemStack projectile = ItemStack.fromNbt(projectiles.getCompound(i));

                        if (projectile.isOf(Items.ECHO_SHARD)) {
                            System.out.println("FOUND ECHO SHARD");
                            return 1.0F;
                        }
                    }
                    return 0.0F;
                }));
    }
}
