package net.moonboy.echoshardsplus.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    @Inject(method = "getHeldProjectiles", at = @At("RETURN"), cancellable = true)
    private void wardenshotGetHeldProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        Predicate<ItemStack> vanilla = cir.getReturnValue();
        cir.setReturnValue(vanilla.or(stack -> stack.isOf(Items.ECHO_SHARD)));
    }

    @Inject(method = "getProjectiles", at = @At("RETURN"), cancellable = true)
    private void wardenshotGetProjectiles(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        Predicate<ItemStack> vanilla = cir.getReturnValue();
        cir.setReturnValue(vanilla.or(stack -> stack.isOf(Items.ECHO_SHARD)));
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void wardenshotUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack crossbow = user.getStackInHand(hand);

        if (EnchantmentHelper.getLevel(ModEnchantments.WARDENSHOT, crossbow) > 0) return;

        ItemStack projectile = user.getProjectileType(crossbow);
        if (projectile.isOf(Items.ECHO_SHARD)) {
            cir.setReturnValue(TypedActionResult.fail(crossbow));
            cir.cancel();
        }
    }

    @Inject(method = "loadProjectiles", at = @At("HEAD"), cancellable = true)
    private static void wardenshotLoadProjectiles(
            LivingEntity shooter,
            ItemStack crossbow,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ItemStack projectile = shooter.getProjectileType(crossbow);
        if (!projectile.isOf(Items.ECHO_SHARD)) return;

        if (EnchantmentHelper.getLevel(ModEnchantments.WARDENSHOT, crossbow) <= 0) {
            cir.setReturnValue(false);
            cir.cancel();
        }
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
}
