package github.mgrlyz.mgsoddities.capabilities.holder.energy;

import github.mgrlyz.mgsoddities.capabilities.holder.IHolder;
import mekanism.api.energy.IEnergyContainer;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IEnergyContainerHolder extends IHolder {

    @NotNull
    List<IEnergyContainer> getEnergyContainers(@Nullable Direction side);
}