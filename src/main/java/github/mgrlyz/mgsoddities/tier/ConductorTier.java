package github.mgrlyz.mgsoddities.tier;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.util.EnumUtils;
import mekanism.api.heat.HeatAPI;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.lib.Color;

public enum ConductorTier {
    PARAGON(BaseTier.PARAGON,5, HeatAPI.DEFAULT_HEAT_CAPACITY, 12_000, Color.rgbad(0.2, 0.2, 0.2, 1)),
    APOTHEOSIS(BaseTier.APOTHEOSIS,5, HeatAPI.DEFAULT_HEAT_CAPACITY, 16_000, Color.rgbad(0.2, 0.2, 0.2, 1));

    private final Color baseColor;
    private final double baseConduction;
    private final double baseHeatCapacity;
    private final double baseConductionInsulation;
    private final BaseTier baseTier;
    private CachedDoubleValue conductionReference;
    private CachedDoubleValue capacityReference;
    private CachedDoubleValue insulationReference;

    ConductorTier(BaseTier tier, double conduction, double heatCapacity, double conductionInsulation, Color color) {
        baseConduction = conduction;
        baseHeatCapacity = heatCapacity;
        baseConductionInsulation = conductionInsulation;

        baseColor = color;
        baseTier = tier;
    }

    public static ConductorTier get(BaseTier tier) {
        for (ConductorTier transmitter : EnumUtils.CONDUCTOR_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return PARAGON;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public double getInverseConduction() {
        return conductionReference == null ? getBaseConduction() : conductionReference.getOrDefault();
    }

    public double getInverseConductionInsulation() {
        return insulationReference == null ? getBaseConductionInsulation() : insulationReference.getOrDefault();
    }

    public double getHeatCapacity() {
        return capacityReference == null ? getBaseHeatCapacity() : capacityReference.getOrDefault();
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public double getBaseConduction() {
        return baseConduction;
    }

    public double getBaseHeatCapacity() {
        return baseHeatCapacity;
    }

    public double getBaseConductionInsulation() {
        return baseConductionInsulation;
    }

    public void setConfigReference(CachedDoubleValue conductionReference, CachedDoubleValue capacityReference, CachedDoubleValue insulationReference) {
        this.conductionReference = conductionReference;
        this.capacityReference = capacityReference;
        this.insulationReference = insulationReference;
    }
}