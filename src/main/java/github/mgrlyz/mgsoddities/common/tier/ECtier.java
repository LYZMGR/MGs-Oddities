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
    private @Nullable CachedLongValue storageReference;
    private @Nullable CachedLongValue outputReference;

    private ECtier(AdvanceTier tier, long max, long out) {
        this.advanceMaxEnergy = max;
        this.advanceOutput = out;
        this.advanceTier = tier;
    }

    public AdvanceTier getAdvanceTier() {
        return this.advanceTier;
    }

    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public long getMaxEnergy() {
        return this.storageReference == null ? this.getAdvanceMaxEnergy() : this.storageReference.getOrDefault();
    }

    public long getOutput() {
        return this.outputReference == null ? this.getAdvanceOutput() : this.outputReference.getOrDefault();
    }

    public long getAdvanceMaxEnergy() {
        return this.advanceMaxEnergy;
    }

    public long getAdvanceOutput() {
        return this.advanceOutput;
    }

    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}
