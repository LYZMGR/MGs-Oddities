package github.mgrlyz.mgsoddities.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.fluids.FluidType;

public class MGsOdditiesGeneralConfig extends BaseMekanismConfig {

    private static final String PUMP_CATEGORY = "pump";
    private final ModConfigSpec configSpec;
    public final CachedIntValue pumpHeavyWaterAmount;

    MGsOdditiesGeneralConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("General Config. This config is synced from server to client.").push("general");

        builder.comment("Pump Settings").push(PUMP_CATEGORY);
        pumpHeavyWaterAmount = CachedIntValue.wrap(this, builder.comment("mB of Heavy Water that is extracted per block of Water by the Electric Pump with a Filter Upgrade.")
                .defineInRange("pumpHeavyWaterAmount", FluidType.BUCKET_VOLUME, FluidType.BUCKET_VOLUME / 10, FluidType.BUCKET_VOLUME));
        builder.pop();

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "MGsOdditiesGeneral";
    }

    @Override
    public String getTranslation() {
        return null;
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
