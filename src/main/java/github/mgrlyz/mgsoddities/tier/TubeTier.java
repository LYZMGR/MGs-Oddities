package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.util.EnumUtils;
import mekanism.common.config.value.CachedLongValue;
import net.neoforged.neoforge.fluids.FluidType;

public enum TubeTier {
    PARAGON(BaseTier.PARAGON, 4_096 * FluidType.BUCKET_VOLUME,1024 * FluidType.BUCKET_VOLUME),
    APOTHEOSIS(BaseTier.APOTHEOSIS,16_384 * FluidType.BUCKET_VOLUME,4_096 * FluidType.BUCKET_VOLUME);

    private final long baseCapacity;
    private final long basePull;
    private final BaseTier baseTier;
    private CachedLongValue capacityReference;
    private CachedLongValue pullReference;

    TubeTier(BaseTier tier, long capacity, long pullAmount) {
        baseCapacity = capacity;
        basePull = pullAmount;
        baseTier = tier;
    }

    public static TubeTier get(BaseTier tier) {
        for (TubeTier transmitter : EnumUtils.TUBE_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return PARAGON;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getTubeCapacity() {
        return capacityReference == null ? getBaseCapacity() : capacityReference.getOrDefault();
    }

    public long getTubePullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getOrDefault();
    }

    public long getBaseCapacity() {
        return baseCapacity;
    }

    public long getBasePull() {
        return basePull;
    }

    public void setConfigReference(CachedLongValue capacityReference, CachedLongValue pullReference) {
        this.capacityReference = capacityReference;
        this.pullReference = pullReference;
    }
}