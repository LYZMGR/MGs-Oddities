package github.mgrlyz.mgsoddities.common.tile.transmitter;

import github.mgrlyz.mgsoddities.api.IMGsOdditiesAlloyInteraction;
import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import mekanism.common.Mekanism;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.content.network.transmitter.BufferedTransmitter;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MGsOdditiesTileEntityTransmitter extends TileEntityTransmitter implements IMGsOdditiesAlloyInteraction {

    public MGsOdditiesTileEntityTransmitter(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected Transmitter<?, ?, ?> createTransmitter(Holder<Block> blockProvider) {
        return null;
    }

    @Override
    public TransmitterType getTransmitterType() {
        return null;
    }

    @NotNull
    protected BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        return current;
    }

    public static void tickServer(Level level, BlockPos blockPos, BlockState blockState, MGsOdditiesTileEntityTransmitter entityTransmitter) {
        entityTransmitter.onUpdateServer();
    }

    @Override
    public void onMGsOdditiesAlloyInteraction(Player player, ItemStack stack, @NotNull IAdvanceTier tier) {
        if (getLevel() != null && getTransmitter().hasTransmitterNetwork()) {
            DynamicNetwork<?, ?, ?> transmitterNetwork = getTransmitter().getTransmitterNetwork();
            List<Transmitter<?, ?, ?>> list = new ArrayList<>(transmitterNetwork.getTransmitters());
            list.sort((o1, o2) -> {
                if (o1 != null && o2 != null) {
                    return Double.compare(o1.getBlockPos().distSqr(worldPosition), o2.getBlockPos().distSqr(worldPosition));
                }
                return 0;
            });
            boolean sharesSet = false;
            int upgraded = 0;
            for (Transmitter<?, ?, ?> transmitter : list) {
                if (transmitter instanceof IMGsOdditiesUpgradeableTransmitter<?> upgradeableTransmitter && upgradeableTransmitter.canUpgrade(tier)) {
                    MGsOdditiesTileEntityTransmitter transmitterTile = (MGsOdditiesTileEntityTransmitter) transmitter.getTransmitterTile();
                    BlockState state = transmitterTile.getBlockState();
                    BlockState upgradeState = transmitterTile.upgradeResult(state, tier.getAdvanceTier());
                    if (state == upgradeState) {
                        continue;
                    }
                    if (!sharesSet) {
                        if (transmitterNetwork instanceof DynamicBufferedNetwork dynamicNetwork) {
                            dynamicNetwork.validateSaveShares((BufferedTransmitter<?, ?, ?, ?>) transmitter);
                        }
                        sharesSet = true;
                    }
                    transmitter.startUpgrading();
                    TransmitterUpgradeData upgradeData = upgradeableTransmitter.getUpgradeData();
                    BlockPos transmitterPos = transmitter.getBlockPos();
                    Level transmitterWorld = transmitter.getLevel();
                    if (upgradeData == null) {
                        Mekanism.logger.warn("Got no upgrade data for transmitter at position: {} in {} but it said it would be able to provide some.",
                                transmitterPos, transmitterWorld);
                    } else {
                        transmitterWorld.setBlockAndUpdate(transmitterPos, upgradeState);
                        MGsOdditiesTileEntityTransmitter upgradedTile = WorldUtils.getTileEntity(MGsOdditiesTileEntityTransmitter.class, transmitterWorld, transmitterPos);
                        if (upgradedTile == null) {
                            Mekanism.logger.warn("Error upgrading transmitter at position: {} in {}.", transmitterPos, transmitterWorld);
                        } else {
                            Transmitter<?, ?, ?> upgradedTransmitter = upgradedTile.getTransmitter();
                            if (upgradedTransmitter instanceof IMGsOdditiesUpgradeableTransmitter) {
                                transferUpgradeData((IMGsOdditiesUpgradeableTransmitter<?>) upgradedTransmitter, upgradeData);
                            } else {
                                Mekanism.logger.warn("Unhandled upgrade data.", new IllegalStateException());
                            }
                            upgraded++;
                            if (upgraded == 8) {
                                break;
                            }
                        }
                    }
                }
            }
            if (upgraded > 0) {
                transmitterNetwork.invalidate(null);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    MekanismCriteriaTriggers.ALLOY_UPGRADE.value().trigger(serverPlayer);
                }
            }
        }
    }

    private <DATA extends TransmitterUpgradeData> void transferUpgradeData(IMGsOdditiesUpgradeableTransmitter<DATA> upgradeableTransmitter, TransmitterUpgradeData data) {
        if (upgradeableTransmitter.dataTypeMatches(data)) {
            upgradeableTransmitter.parseUpgradeData((DATA) data);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new IllegalStateException());
        }
    }
}