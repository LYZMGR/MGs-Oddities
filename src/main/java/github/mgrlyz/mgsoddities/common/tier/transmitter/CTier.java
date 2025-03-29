package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.CableTier;

public class CTier {
    public CTier() {
    }

    public static long getCapacityAsLong(CableTier tier) {
        if (tier == null) {
            return 8000L;
        } else {
            return switch (tier) {
                case BASIC, ADVANCED, ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonUniversalCableCapacity.get();
                case ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisUniversalCableCapacity.get();
            };
        }
    }
}
