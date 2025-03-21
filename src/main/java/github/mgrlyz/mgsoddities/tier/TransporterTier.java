package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.util.EnumUtils;
import mekanism.common.config.value.CachedIntValue;

public enum TransporterTier {
    PARAGON(BaseTier.PARAGON,128,100),
    APOTHEOSIS(BaseTier.APOTHEOSIS,256,200);

    private final int basePull;
    private final int baseSpeed;
    private final BaseTier baseTier;
    private CachedIntValue pullReference;
    private CachedIntValue speedReference;

    TransporterTier(BaseTier tier, int pull, int s) {
        basePull = pull;
        baseSpeed = s;
        baseTier = tier;
    }

    public static TransporterTier get(BaseTier tier) {
        for (TransporterTier transmitter : EnumUtils.TRANSPORTER_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return PARAGON;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getPullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getOrDefault();
    }

    //TODO - 1.21: Figure this out as speed is configured as per half second??
    public int getSpeed() {
        return speedReference == null ? getBaseSpeed() : speedReference.getOrDefault();
    }

    public int getBasePull() {
        return basePull;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public void setConfigReference(CachedIntValue pullReference, CachedIntValue speedReference) {
        this.pullReference = pullReference;
        this.speedReference = speedReference;
    }
}