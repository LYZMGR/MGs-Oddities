package github.mgrlyz.mgsoddities.intergration.energy.fluxnetworks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.integration.energy.fluxnetworks.FNIntegration;
import mekanism.common.integration.energy.fluxnetworks.FNStrictEnergyHandler;
import mekanism.common.util.UnitDisplayUtils;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

@NothingNullByDefault
public class FNEnergyCompat implements IEnergyCompat {

    @Override
    public MultiTypeCapability<IFNEnergyStorage> getCapability() {
        return FNCapability.ENERGY;
    }

    @Override
    public boolean isUsable() {
        return capabilityExists() && isConfigEnabled();
    }

    private boolean isConfigEnabled() {
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.isEnabled() && !MekanismConfig.general.blacklistFluxNetworks.getOrDefault();
    }

    @Override
    public boolean capabilityExists() {
        return Mekanism.hooks.FluxNetworksLoaded;
    }

    @Override
    public <OBJECT, CONTEXT> ICapabilityProvider<OBJECT, CONTEXT, ?> getProviderAs(ICapabilityProvider<OBJECT, CONTEXT, IStrictEnergyHandler> provider) {
        return (obj, ctx) -> {
            IStrictEnergyHandler handler = provider.getCapability(obj, ctx);
            return handler != null && isConfigEnabled() ? wrapStrictEnergyHandler(handler) : null;
        };
    }

    @Override
    public Object wrapStrictEnergyHandler(IStrictEnergyHandler handler) {
        return new FNIntegration(handler);
    }

    @Override
    public IStrictEnergyHandler wrapAsStrictEnergyHandler(Object handler) {
        return new FNStrictEnergyHandler((IFNEnergyStorage) handler);
    }
}