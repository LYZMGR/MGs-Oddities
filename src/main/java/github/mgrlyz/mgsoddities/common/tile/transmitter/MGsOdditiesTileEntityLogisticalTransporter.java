package github.mgrlyz.mgsoddities.common.tile.transmitter;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesLogisticalTransporter;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.client.model.data.TransmitterModelData;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesTileEntityLogisticalTransporter extends MGsOdditiesTileEntityLogisticalTransporterBase {
    public MGsOdditiesTileEntityLogisticalTransporter(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected MGsOdditiesLogisticalTransporter createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesLogisticalTransporter(blockProvider, this);
    }

    @Override
    public MGsOdditiesLogisticalTransporter getTransmitter() {
        return (MGsOdditiesLogisticalTransporter) super.getTransmitter();
    }

    @Override
    public TransmitterType getTransmitterType() {
        return TransmitterType.LOGISTICAL_TRANSPORTER;
    }

    @Override
    protected void updateModelData(TransmitterModelData modelData) {
        super.updateModelData(modelData);
        modelData.setHasColor(getTransmitter().getColor() != null);
    }

    @NotNull
    @Override
    protected BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        return BlockStateHelper.copyStateData(current, switch (tier) {
            case PARAGON -> MGsOdditiesBlocks.PARAGON_LOGISTICAL_TRANSPORTER;
            case APOTHEOSIS -> MGsOdditiesBlocks.APOTHEOSIS_LOGISTICAL_TRANSPORTER;
        });
    }
}