package net.moonboy.echoshardsplus.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WardenshotEnchantment extends Enchantment {
    public WardenshotEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(Items.CROSSBOW);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public static void fireSonicBoom(LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        Vec3d lookDir = shooter.getRotationVecClient();
        double range = 7.0;
        Vec3d endPos = eyePos.add(lookDir.multiply(range));
        Box searchBox = new Box(eyePos, endPos).expand(2.0);

        List<LivingEntity> targets = shooter.getWorld().getEntitiesByClass(LivingEntity.class, searchBox, entity -> entity != shooter && entity.isAlive());

        for (LivingEntity target : targets) {
            Vec3d toTarget = target.getPos().subtract(eyePos).normalize();
            if (toTarget.dotProduct(lookDir) < 0.5) {
                continue;
            }

            target.damage(shooter.getWorld().getDamageSources().sonicBoom(shooter), 10f);

            Vec3d knockbackDir = target.getPos().subtract(shooter.getPos()).normalize();

            target.addVelocity(knockbackDir.x * 3.0, 0.5, knockbackDir.z * 3.0
            );

            target.velocityModified = true;
        }

        if (shooter instanceof PlayerEntity player) {
            Vec3d recoilDir = lookDir.negate();

            player.setVelocity(recoilDir.x * 1.5, player.getVelocity().y + 0.3, recoilDir.z *1.5);
            player.velocityModified = true;
        }

        shooter.getWorld().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0f, 1.0f);
        shooter.getWorld().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 0.8f, 1.0f);

        if (shooter.getWorld() instanceof ServerWorld serverWorld) {
            for (double dist = 1.0; dist <= range; dist += 0.5) {
                Vec3d particlePos = eyePos.add(lookDir.multiply(dist));
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.2, 0.2, 0.2, 0.0);
            }
        }
    }
}
