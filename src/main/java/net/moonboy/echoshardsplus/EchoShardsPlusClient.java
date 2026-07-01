package net.moonboy.echoshardsplus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EchoShardsPlusClient implements ClientModInitializer {

    public static final Identifier CROSSBOW_ECHO_LOADED =
            new Identifier(EchoShardsPlus.MOD_ID, "item/crossbow_echo_loaded");

    @Override
    public void onInitializeClient() {
        EchoShardsPlus.LOGGER.info("Client initializer running for " + EchoShardsPlus.MOD_ID);

        ModelPredicateProviderRegistry.register(
                Items.CROSSBOW,
                new Identifier(EchoShardsPlus.MOD_ID, "echo_loaded"),
                (stack, world, entity, seed) -> {
                    if (!CrossbowItem.isCharged(stack)) return 0.0f;
                    NbtCompound nbt = stack.getNbt();
                    if (nbt == null) return 0.0f;
                    if (!nbt.contains("ChargedProjectiles")) return 0.0f;
                    NbtList projectiles = nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
                    for (int i = 0; i < projectiles.size(); i++) {
                        ItemStack projectile = ItemStack.fromNbt(projectiles.getCompound(i));
                        if (projectile.isOf(Items.ECHO_SHARD)) return 1.0f;
                    }
                    return 0.0f;
                }
        );

        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(CROSSBOW_ECHO_LOADED);
            pluginContext.modifyModelOnLoad().register((original, context) -> {
                Identifier id = context.id();
                if (id == null) return original;
                if (!id.toString().equals("minecraft:crossbow#inventory")) return original;
                if (!(original instanceof JsonUnbakedModel jsonModel)) return original;
                List<ModelOverride.Condition> conditions = new ArrayList<>();
                conditions.add(new ModelOverride.Condition(
                        new Identifier("charged"), 1.0f));
                conditions.add(new ModelOverride.Condition(
                        new Identifier(EchoShardsPlus.MOD_ID, "echo_loaded"), 1.0f));

                List<ModelOverride> overrides = new ArrayList<>(jsonModel.getOverrides());
                overrides.add(new ModelOverride(CROSSBOW_ECHO_LOADED, conditions));
                JsonUnbakedModel result = new JsonUnbakedModel(
                        jsonModel.parentId,
                        List.of(),
                        jsonModel.textureMap,
                        null,
                        null,
                        jsonModel.getTransformations(),
                        overrides
                );
                result.id = jsonModel.id;

                EchoShardsPlus.LOGGER.info(
                        "Injected echo_loaded override into crossbow model. " + "Total overrides: " + overrides.size());
                return result;
            });
        });
    }
}