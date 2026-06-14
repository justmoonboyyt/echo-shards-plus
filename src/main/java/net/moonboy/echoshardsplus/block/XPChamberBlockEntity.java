package net.moonboy.echoshardsplus.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class XPChamberBlockEntity extends BlockEntity {
    private int storedXP = 0;
    private static final double ABSORB_RADIUS = 5.0;
    private int tickCounter = 0;
    private static final int SCAN_INTERVAL = 10;

    public XPChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.XP_CHAMBER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, XPChamberBlockEntity blockEntity) {
        blockEntity.tickCounter++;

        if (blockEntity.tickCounter < SCAN_INTERVAL) return;
        blockEntity.tickCounter = 0;

        Box searchBox = new Box(
                pos.getX() - 1, pos.getY() +1, pos.getZ() -1,
                pos.getX() + 2, pos.getY() + 4, pos.getZ() +2
        );

        List<ExperienceOrbEntity> orbs = world.getEntitiesByType(EntityType.EXPERIENCE_ORB, searchBox, entity -> true);

        if (orbs.isEmpty()) return;

        boolean absorbed = false;
        for (ExperienceOrbEntity orb : orbs) {
            blockEntity.storedXP += orb.getExperienceAmount();

            orb.discard();
            absorbed=true;
        }

        if (absorbed) {
            blockEntity.markDirty();
            world.updateListeners(pos, state, state, 0);
        }
    }

    public void addStoredXP(int xp) {
        storedXP = Math.max(0, storedXP + xp);
    }

    public void setStoredXP(int xp) {
        storedXP = Math.max(0, xp);
    }

    public int getStoredXP() {
        return storedXP;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("StoredXP", storedXP);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.storedXP = nbt.getInt("StoredXP");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
