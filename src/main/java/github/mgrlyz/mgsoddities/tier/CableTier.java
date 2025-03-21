package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.util.EnumUtils;
import mekanism.common.config.value.CachedLongValue;
import org.jetbrains.annotations.Nullable;

public enum CableTier {
    PARAGON(BaseTier.PARAGON,65_536L),
    APOTHEOSIS(BaseTier.APOTHEOSIS,524_288L);

    private final long baseCapacity;
    private final BaseTier baseTier;
    @Nullable
    private CachedLongValue capacityReference;

    CableTier(BaseTier tier, long capacity) {
        baseCapacity = capacity;
        baseTier = tier;
    }

    public static CableTier get(BaseTier tier) {
        for (CableTier transmitter : EnumUtils.CABLE_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return PARAGON;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getCableCapacity() {
        return capacityReference == null ? getBaseCapacity() : capacityReference.getOrDefault();
    }

    public long getBaseCapacity() {
        return baseCapacity;
    }

    public void setConfigReference(CachedLongValue capacityReference) {
        this.capacityReference = capacityReference;
    }
}