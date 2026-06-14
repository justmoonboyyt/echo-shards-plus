package net.moonboy.echoshardsplus.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WardenshotEnchantment extends Enchantment {
    public WardenshotEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (!user.getWorld().isClient()){
            ServerWorld world = (ServerWorld)user.getWorld();
            BlockPos pos = target.getBlockPos();

            if(level == 1) {
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
                EntityType.WARDEN.spawn(world, pos, SpawnReason.TRIGGERED);
            }
        }
        super.onTargetDamaged(user, target, level);
    }
}
