package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.util.EnumUtils;
import mekanism.common.config.value.CachedIntValue;
import net.neoforged.neoforge.fluids.FluidType;

public enum PipeTier {
    PARAGON(BaseTier.PARAGON,512 * FluidType.BUCKET_VOLUME,128 * FluidType.BUCKET_VOLUME),
    APOTHEOSIS(BaseTier.APOTHEOSIS,2048 * FluidType.BUCKET_VOLUME,512 * FluidType.BUCKET_VOLUME);


    private final int baseCapacity;
    private final int basePull;
    private final BaseTier baseTier;
    private CachedIntValue capacityReference;
    private CachedIntValue pullReference;

    PipeTier(BaseTier tier, int capacity, int pullAmount) {
        baseCapacity = capacity;
        basePull = pullAmount;
        baseTier = tier;
    }

    public static PipeTier get(BaseTier tier) {
        for (PipeTier transmitter : EnumUtils.PIPE_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return PARAGON;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getPipeCapacity() {
        return capacityReference == null ? getBaseCapacity() : capacityReference.getOrDefault();
    }

    public int getPipePullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getOrDefault();
    }

    public int getBaseCapacity() {
        return baseCapacity;
    }

    public int getBasePull() {
        return basePull;
    }

    public void setConfigReference(CachedIntValue capacityReference, CachedIntValue pullReference) {
        this.capacityReference = capacityReference;
        this.pullReference = pullReference;
    }
}