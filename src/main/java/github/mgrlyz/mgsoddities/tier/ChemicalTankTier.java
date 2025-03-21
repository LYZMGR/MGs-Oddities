package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import mekanism.common.config.value.CachedLongValue;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public enum ChemicalTankTier {
    PARAGON(BaseTier.PARAGON,32_768 * FluidType.BUCKET_VOLUME,4_096 * FluidType.BUCKET_VOLUME),
    APOTHEOSIS(BaseTier.APOTHEOSIS,131_072 * FluidType.BUCKET_VOLUME,32_768 * FluidType.BUCKET_VOLUME);

    private final long baseStorage;
    private final long baseOutput;
    private final BaseTier baseTier;
    private CachedLongValue storageReference;
    private CachedLongValue outputReference;

    ChemicalTankTier(BaseTier tier, long s, long o) {
        baseStorage = s;
        baseOutput = o;
        baseTier = tier;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public long getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getOrDefault();
    }

    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getOrDefault();
    }

    public long getBaseStorage() {
        return baseStorage;
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}