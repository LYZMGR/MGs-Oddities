package github.mgrlyz.mgsoddities.common.capabilities.energy;

import github.mgrlyz.mgsoddities.common.tier.ECtier;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.LongSupplier;

public class MGsOdditiesEnergyCubeEnergyContainer extends BasicEnergyContainer {
    private final boolean isCreative = false;
    private final LongSupplier rate;
    public static MGsOdditiesEnergyCubeEnergyContainer create(ECtier tier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Energy cube tier cannot be null");
        return new MGsOdditiesEnergyCubeEnergyContainer(tier, listener);
    }

    protected MGsOdditiesEnergyCubeEnergyContainer(ECtier tier, @Nullable IContentsListener listener) {
        super(tier.getMaxEnergy(), ConstantPredicates.alwaysTrue(), ConstantPredicates.alwaysTrue(), listener);
        Objects.requireNonNull(tier);
        this.rate = tier::getOutput;
    }

    protected long getInsertRate(@Nullable AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? this.rate.getAsLong() : super.getInsertRate(automationType);
    }

    public long getExtractRate(AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? this.rate.getAsLong() : super.getExtractRate(automationType);
    }

    public long insert(long amount, Action action, @NotNull AutomationType automationType) {
        return super.insert(amount, action.combine(!this.isCreative), automationType);
    }

    public long extract(long amount, Action action, @NotNull AutomationType automationType) {
        return super.extract(amount, action.combine(!this.isCreative), automationType);
    }
}