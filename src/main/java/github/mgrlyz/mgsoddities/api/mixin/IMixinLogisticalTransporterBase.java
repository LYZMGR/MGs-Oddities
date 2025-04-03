package github.mgrlyz.mgsoddities.api.mixin;

import mekanism.common.content.transporter.TransporterStack;

public interface IMixinLogisticalTransporterBase {
    void mekanismMGsOddities$getEntity(TransporterStack stack, int progress);
}