package github.mgrlyz.mgsoddities.capabilities;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@FunctionalInterface
public interface ICapabilityAware {

    void attachCapabilities(RegisterCapabilitiesEvent event);
}