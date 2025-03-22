package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.config.value.CachedLongValue;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@NothingNullByDefault
public enum EnergyCubeTier implements ITier, StringRepresentable {
    PARAGON(BaseTier.PARAGON, 1_024_000_000L, 1_024_000L),
    APOTHEOSIS(BaseTier.APOTHEOSIS, 4_096_000_000L, 4_096_000L);

    private final long baseMaxEnergy;
    private final long baseOutput;
    private final BaseTier baseTier;
    @Nullable
    private CachedLongValue storageReference;
    @Nullable
    private CachedLongValue outputReference;

    EnergyCubeTier(BaseTier tier, long max, long out) {
        baseMaxEnergy = max;
        baseOutput = out;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public long getMaxEnergy() {
        return storageReference == null ? getBaseMaxEnergy() : storageReference.getOrDefault();
    }

    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getOrDefault();
    }

    public long getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}