package github.mgrlyz.mgsoddities.common.tier;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import mekanism.common.config.value.CachedIntValue;

public enum FTTier implements IAdvanceTier {
    PARAGON(AdvanceTier.PARAGON, 4_096_000, 2_048_000),
    APOTHEOSIS(AdvanceTier.APOTHEOSIS, 32_768_000, 16_384_000);

    private final int advanceStorage;
    private final int advanceOutput;
    private final AdvanceTier advanceTier;
    private CachedIntValue storageReference;
    private CachedIntValue outputReference;

    FTTier(AdvanceTier tier, int s, int o) {
        advanceStorage = s;
        advanceOutput = o;
        advanceTier = tier;
    }

    @Override
    public AdvanceTier getAdvanceTier() {
        return advanceTier;
    }

    public int getStorage() {
        return storageReference == null ? getAdvanceStorage() : storageReference.getOrDefault();
    }

    public int getOutput() {
        return outputReference == null ? getAdvanceOutput() : outputReference.getOrDefault();
    }

    public int getAdvanceStorage() {
        return advanceStorage;
    }

    public int getAdvanceOutput() {
        return advanceOutput;
    }

    public void setConfigReference(CachedIntValue storageReference, CachedIntValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}