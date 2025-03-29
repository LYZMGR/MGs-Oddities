package github.mgrlyz.mgsoddities.common.tile.transmitter;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesLogisticalTransporter;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.client.model.data.TransmitterModelData;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesTileEntityLogisticalTransporter extends MGsOdditiesTileEntityLogisticalTransporterBase {
    public MGsOdditiesTileEntityLogisticalTransporter(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    protected MGsOdditiesLogisticalTransporter createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesLogisticalTransporter(blockProvider, this);
    }

    public MGsOdditiesLogisticalTransporter getTransmitter() {
        return (MGsOdditiesLogisticalTransporter)super.getTransmitter();
    }

    public TransmitterType getTransmitterType() {
        return TransmitterType.LOGISTICAL_TRANSPORTER;
    }

    protected void updateModelData(TransmitterModelData modelData) {
        super.updateModelData(modelData);
        modelData.setHasColor(this.getTransmitter().getColor() != null);
    }

    protected @NotNull BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        BlockRegistryObject var10001;
        switch (tier) {
            case PARAGON -> var10001 = MGsOdditiesBlocks.PARAGON_LOGISTICAL_TRANSPORTER;
            case APOTHEOSIS -> var10001 = MGsOdditiesBlocks.APOTHEOSIS_LOGISTICAL_TRANSPORTER;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return BlockStateHelper.copyStateData(current, var10001);
    }
}