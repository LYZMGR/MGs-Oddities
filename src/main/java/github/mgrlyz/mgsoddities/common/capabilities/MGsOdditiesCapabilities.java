package github.mgrlyz.mgsoddities.common.capabilities;

import github.mgrlyz.mgsoddities.api.IMGsOdditiesAlloyInteraction;
import mekanism.common.Mekanism;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class MGsOdditiesCapabilities {
    public static final BlockCapability<IMGsOdditiesAlloyInteraction, @Nullable Direction> MGSODDITIES_ALLOY_INTERACTION = BlockCapability.createSided(Mekanism.rl("extra_alloy_interaction"), IMGsOdditiesAlloyInteraction.class);

    private MGsOdditiesCapabilities() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    }
}