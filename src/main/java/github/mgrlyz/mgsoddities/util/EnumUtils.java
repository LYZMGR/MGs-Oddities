package github.mgrlyz.mgsoddities.util;

import github.mgrlyz.mgsoddities.api.tier.BaseTier;
import github.mgrlyz.mgsoddities.tier.*;

public class EnumUtils {
    public static final BaseTier[] TIERS = BaseTier.values();
    public static final TubeTier[] TUBE_TIERS = TubeTier.values();
    public static final CableTier[] CABLE_TIERS = CableTier.values();
    public static final TransporterTier[] TRANSPORTER_TIERS = TransporterTier.values();
    public static final ConductorTier[] CONDUCTOR_TIERS = ConductorTier.values();
    public static final PipeTier[] PIPE_TIERS = PipeTier.values();

    public static final UnitDisplayUtils.MeasurementUnit[] MEASUREMENT_UNITS = UnitDisplayUtils.MeasurementUnit.values();
}