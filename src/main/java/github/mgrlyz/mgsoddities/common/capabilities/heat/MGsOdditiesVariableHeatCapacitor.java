package github.mgrlyz.mgsoddities.common.capabilities.heat;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.heat.HeatAPI;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

@NothingNullByDefault
public class MGsOdditiesVariableHeatCapacitor extends BasicHeatCapacitor {

    private final double conductionCoefficientSupplier;
    private final double insulationCoefficientSupplier;

    public static MGsOdditiesVariableHeatCapacitor create(double heatCapacity, @Nullable DoubleSupplier ambientTempSupplier, @Nullable IContentsListener listener) {
        return create(heatCapacity, () -> HeatAPI.DEFAULT_INVERSE_CONDUCTION, () -> HeatAPI.DEFAULT_INVERSE_INSULATION, ambientTempSupplier, listener);
    }

    public static MGsOdditiesVariableHeatCapacitor create(double heatCapacity, DoubleSupplier conductionCoefficient1, DoubleSupplier insulationCoefficient1,
                                                    @Nullable DoubleSupplier ambientTempSupplier, @Nullable IContentsListener listener) {
        return new MGsOdditiesVariableHeatCapacitor(heatCapacity, conductionCoefficient1, insulationCoefficient1, ambientTempSupplier, listener);
    }

    public static MGsOdditiesVariableHeatCapacitor create(double heatCapacity, double conductionCoefficient, double insulationCoefficient,
                                                    @Nullable DoubleSupplier ambientTempSupplier, @Nullable IContentsListener listener) {
        return new MGsOdditiesVariableHeatCapacitor(heatCapacity, conductionCoefficient, insulationCoefficient, ambientTempSupplier, listener);
    }

    protected MGsOdditiesVariableHeatCapacitor(double heatCapacity, DoubleSupplier conductionCoefficient, DoubleSupplier insulationCoefficient,
                                         @Nullable DoubleSupplier ambientTempSupplier, @Nullable IContentsListener listener) {
        super(heatCapacity, conductionCoefficient.getAsDouble(), insulationCoefficient.getAsDouble(), ambientTempSupplier, listener);
        this.conductionCoefficientSupplier = conductionCoefficient.getAsDouble();
        this.insulationCoefficientSupplier = insulationCoefficient.getAsDouble();
    }

    protected MGsOdditiesVariableHeatCapacitor(double heatCapacity, double conductionCoefficient, double insulationCoefficient,
                                         @Nullable DoubleSupplier ambientTempSupplier, @Nullable IContentsListener listener) {
        super(heatCapacity, conductionCoefficient, insulationCoefficient, ambientTempSupplier, listener);
        this.conductionCoefficientSupplier = conductionCoefficient;
        this.insulationCoefficientSupplier = insulationCoefficient;
    }

    @Override
    public double getInverseConduction() {
        return Math.max(1, conductionCoefficientSupplier);
    }

    @Override
    public double getInverseInsulation() {
        return insulationCoefficientSupplier;
    }
}