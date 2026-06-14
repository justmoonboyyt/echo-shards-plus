package net.moonboy.echoshardsplus.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class XPChamberBlock extends BlockWithEntity {
    public static final BooleanProperty COLLECTING = BooleanProperty.of("collecting");

    public XPChamberBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(COLLECTING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLLECTING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new XPChamberBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;

        return checkType(type, ModBlockEntities.XP_CHAMBER_BE, XPChamberBlockEntity::tick);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(COLLECTING)) {
            world.setBlockState(pos, state.with(COLLECTING, false));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof XPChamberBlockEntity chamber) {
                if (player.isSneaking()) {
                    int totalXP = calculateTotalXP(player);
                    if (totalXP > 0) {
                        player.experienceLevel = 0;
                        player.experienceProgress = 0f;
                        player.totalExperience = 0;

                        chamber.addStoredXP(totalXP);
                    }
                }
                else {
                    int currentLevel = player.experienceLevel;
                    int partialXP = Math.round(player.experienceProgress * player.getNextLevelExperience());
                    if (partialXP > 0) {
                        player.experienceProgress = 0f;
                        player.totalExperience = Math.max(0, player.totalExperience - partialXP);
                        chamber.addStoredXP(partialXP);
                    }
                    if (currentLevel >= 1){
                        int xpForOneLevel = getXPNeededForLevel(currentLevel - 1);
                        player.experienceLevel = currentLevel - 1;
                        player.experienceProgress = 0f;
                        player.totalExperience = Math.max(0, player.totalExperience - xpForOneLevel);
                        chamber.addStoredXP(xpForOneLevel);
                    }
                  }
                chamber.markDirty();
                pulseCollecting(state, world, pos);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof XPChamberBlockEntity chamber) {
                int stored = chamber.getStoredXP();

                if (stored <= 0) {
                    return;
                }

                if (player.isSneaking()) {
                    player.addExperience(stored);
                    chamber.setStoredXP(0);
                }
                else {
                    int xpForNextLevel = getXPNeededForLevel(player.experienceLevel);
                    if (stored >= xpForNextLevel) {
                        player.addExperience(xpForNextLevel);
                        chamber.addStoredXP(-xpForNextLevel);
                    }
                    else {
                        player.addExperience(stored);
                        chamber.setStoredXP(0);
                    }
                }
                chamber.markDirty();
                pulseCollecting(state, world, pos);
            }
        }
    }

    void pulseCollecting(BlockState state, World world, BlockPos pos) {
        if (!state.get(COLLECTING)) {
            world.setBlockState(pos, state.with(COLLECTING, true));
        }
        world.scheduleBlockTick(pos, this, 1, TickPriority.NORMAL);
    }

    private int calculateTotalXP(PlayerEntity player) {
        int total = 0;

        for (int lvl = 0; lvl < player.experienceLevel; lvl++) {
            total += getXPNeededForLevel(lvl);
        }

        total += Math.round(player.experienceProgress * player.getNextLevelExperience());
        return total;
    }

    private int getXPNeededForLevel(int level) {
        if (level <= 15) {
            return 2 * level +7;
        }
        else if (level <= 30) {
            return 5 * level - 38;
        }
        else {
            return 9 * level - 158;
        }
    }
}
