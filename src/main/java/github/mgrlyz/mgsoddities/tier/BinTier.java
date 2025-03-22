package github.mgrlyz.mgsoddities.tier;


import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.config.value.CachedIntValue;

public enum BinTier implements ITier {
    PARAGON(BaseTier.PARAGON, 2_097_152),
    APOTHEOSIS(BaseTier.APOTHEOSIS, 33_554_432);

    private final int baseStorage;
    private final BaseTier baseTier;
    private CachedIntValue storageReference;

    BinTier(BaseTier tier, int s) {
        baseTier = tier;
        baseStorage = s;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getOrDefault();
    }

    public int getBaseStorage() {
        return baseStorage;
    }

    public void setConfigReference(CachedIntValue storageReference) {
        this.storageReference = storageReference;
    }
}