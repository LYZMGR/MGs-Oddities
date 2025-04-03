package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.CableTier;

public class CTier {

    public static long getCapacityAsLong(CableTier tier) {
        if (tier == null) return 8000L;
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonUniversalCableCapacity.get();
                case ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisUniversalCableCapacity.get();
        };
    }
}