package net.moonboy.echoshardsplus.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.moonboy.echoshardsplus.enchantment.ModEnchantments;
import net.moonboy.echoshardsplus.enchantment.WardenshotEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void wardenshotUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack crossbow = user.getStackInHand(hand);

        if (EnchantmentHelper.getLevel(ModEnchantments.WARDENSHOT, crossbow) <= 0) return;

        if (CrossbowItem.isCharged(crossbow)) return;

        ItemStack echoShard = findEchoShard(user);

        if(echoShard.isEmpty()){
            cir.setReturnValue(TypedActionResult.fail(crossbow));
            cir.cancel();
            return;
        }
        user.setCurrentHand(hand);
        cir.setReturnValue(TypedActionResult.consume(crossbow));
        cir.cancel();
    }

    @Inject(method = "loadProjectiles", at = @At("HEAD"), cancellable = true)
    private static void wardenshotLoadProjectiles(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentHelper.getLevel(ModEnchantments.WARDENSHOT, crossbow) <= 0) return;
        if (!(shooter instanceof PlayerEntity player)) return;

        ItemStack echoShard = findEchoShard(player);
        if (echoShard.isEmpty()){
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
        CrossbowItem.setCharged(crossbow, true);
        putEchoShardProjectile(crossbow, echoShard.copy().split(1));
        if (!player.getAbilities().creativeMode) {
            echoShard.decrement(1);
        }

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "shoot", at = @At("HEAD"), cancellable = true)
    private static void wardenshotShoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci) {
        if (!projectile.isOf(Items.ECHO_SHARD)) return;
        if (EnchantmentHelper.getLevel(ModEnchantments.WARDENSHOT, crossbow) <= 0) return;

        ci.cancel();

        if (world.isClient()) return;

        WardenshotEnchantment.fireSonicBoom(shooter);

        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private static ItemStack findEchoShard(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (stack.isOf(Items.ECHO_SHARD)) return stack;
        }
        if (player.getOffHandStack().isOf(Items.ECHO_SHARD)) {
            return player.getOffHandStack();
        }
        return ItemStack.EMPTY;
    }

    private static void putEchoShardProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbt = crossbow.getOrCreateNbt();
        NbtList list;
        if (nbt.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
            list = nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
        } else {
            list = new NbtList();
        }
        NbtCompound projectileNbt = new NbtCompound();
        projectile.writeNbt(projectileNbt);
        list.add(projectileNbt);
        nbt.put("ChargedProjectiles", list);
    }
}
