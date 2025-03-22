package github.mgrlyz.mgsoddities.intergration.energy;

import github.mgrlyz.mgsoddities.capabilities.Capabilities;
import github.mgrlyz.mgsoddities.capabilities.MultiTypeCapability;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

@NothingNullByDefault
public class StrictEnergyCompat implements IEnergyCompat {

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public MultiTypeCapability<IStrictEnergyHandler> getCapability() {
        return Capabilities.STRICT_ENERGY;
    }

    @Override
    public <OBJECT, CONTEXT> ICapabilityProvider<OBJECT, CONTEXT, IStrictEnergyHandler> getProviderAs(ICapabilityProvider<OBJECT, CONTEXT, IStrictEnergyHandler> provider) {
        return provider;
    }

    @Override
    public IStrictEnergyHandler wrapStrictEnergyHandler(IStrictEnergyHandler handler) {
        return handler;
    }

    @Override
    public IStrictEnergyHandler wrapAsStrictEnergyHandler(Object handler) {
        return (IStrictEnergyHandler) handler;
    }
}