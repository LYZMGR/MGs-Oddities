package github.mgrlyz.mgsoddities.common.util;

import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import mekanism.api.tier.ITier;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import org.jetbrains.annotations.NotNull;

public interface IMGsOdditiesUpgradeableTransmitter<DATA extends TransmitterUpgradeData> {
    DATA getUpgradeData();

    boolean dataTypeMatches(@NotNull TransmitterUpgradeData var1);

    void parseUpgradeData(@NotNull DATA var1);

    ITier getTier();

    default <TIER extends IAdvanceTier> boolean canUpgrade(TIER alloyTier) {
        return alloyTier.getAdvanceTier().ordinal() == this.getTier().getBaseTier().ordinal() + 1;
    }
}
