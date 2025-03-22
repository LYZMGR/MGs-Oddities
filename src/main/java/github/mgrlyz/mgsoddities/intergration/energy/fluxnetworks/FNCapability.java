package github.mgrlyz.mgsoddities.intergration.energy.fluxnetworks;

import github.mgrlyz.mgsoddities.capabilities.MultiTypeCapability;
import github.mgrlyz.mgsoddities.intergration.MGsOdditiesHooks;
import net.minecraft.resources.ResourceLocation;

public class FNCapability {
    static final MultiTypeCapability<IFNEnergyStorage> ENERGY = new MultiTypeCapability<>(ResourceLocation.fromNamespaceAndPath(MGsOdditiesHooks.FLUX_NETWORKS_MOD_ID, "energy_handler"), IFNEnergyStorage.class);
}