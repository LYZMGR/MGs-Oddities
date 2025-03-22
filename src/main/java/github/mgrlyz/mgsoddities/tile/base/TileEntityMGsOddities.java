package github.mgrlyz.mgsoddities.tile.base;


import mekanism.common.capabilities.resolver.manager.EnergyHandlerManager;
import org.jetbrains.annotations.Nullable;

public class TileEntityMGsOddities extends CapabilityTileEntity {

    //Variables for handling IMekanismStrictEnergyHandler
    @Nullable
    private final EnergyHandlerManager energyHandlerManager;

    @Override
    public final boolean canHandleEnergy() {
        return energyHandlerManager != null && energyHandlerManager.canHandle();
    }
}
