package github.mgrlyz.mgsoddities.common.tier;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.config.value.CachedLongValue;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@NothingNullByDefault
public enum ECtier implements IAdvanceTier, StringRepresentable {
    PARAGON(AdvanceTier.PARAGON, 1024000000L, 1024000L),
    APOTHEOSIS(AdvanceTier.APOTHEOSIS, 4096000000L, 4096000L);

    private final long advanceMaxEnergy;
    private final long advanceOutput;
    private final AdvanceTier advanceTier;
    @Nullable
    private CachedLongValue storageReference;
    @Nullable
    private CachedLongValue outputReference;

    ECtier(AdvanceTier tier, long max, long out) {
        advanceMaxEnergy = max;
        advanceOutput = out;
        advanceTier = tier;
    }

    @Override
    public AdvanceTier getAdvanceTier() {
        return advanceTier;
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public long getMaxEnergy() {
        return storageReference == null ? getAdvanceMaxEnergy() : storageReference.getOrDefault();
    }

    public long getOutput() {
        return outputReference == null ? getAdvanceOutput() : outputReference.getOrDefault();
    }

    public long getAdvanceMaxEnergy() {
        return advanceMaxEnergy;
    }

    public long getAdvanceOutput() {
        return advanceOutput;
    }

    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}