package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.config.value.CachedIntValue;
import net.neoforged.neoforge.fluids.FluidType;

public enum FluidTankTier implements ITier {
    PARAGON(BaseTier.PARAGON, 512 * FluidType.BUCKET_VOLUME, 256 * FluidType.BUCKET_VOLUME),
    APOTHEOSIS(BaseTier.APOTHEOSIS, 1_024 * FluidType.BUCKET_VOLUME, 512 * FluidType.BUCKET_VOLUME);

    private final int baseStorage;
    private final int baseOutput;
    private final BaseTier baseTier;
    private CachedIntValue storageReference;
    private CachedIntValue outputReference;

    FluidTankTier(BaseTier tier, int s, int o) {
        baseStorage = s;
        baseOutput = o;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getOrDefault();
    }

    public int getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getOrDefault();
    }

    public int getBaseStorage() {
        return baseStorage;
    }

    public int getBaseOutput() {
        return baseOutput;
    }

    public void setConfigReference(CachedIntValue storageReference, CachedIntValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}