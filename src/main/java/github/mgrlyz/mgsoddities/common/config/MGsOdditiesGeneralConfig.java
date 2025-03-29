package github.mgrlyz.mgsoddities.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MGsOdditiesGeneralConfig extends BaseMekanismConfig {
    private static final String PUMP_CATEGORY = "pump";
    private final ModConfigSpec configSpec;
    public final CachedIntValue pumpHeavyWaterAmount;

    MGsOdditiesGeneralConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("General Config. This config is synced from server to client.").push("general");
        builder.comment("Pump Settings").push("pump");
        this.pumpHeavyWaterAmount = CachedIntValue.wrap(this, builder.comment("mB of Heavy Water that is extracted per block of Water by the Electric Pump with a Filter Upgrade.").defineInRange("pumpHeavyWaterAmount", 1000, 100, 1000));
        builder.pop();
        builder.pop();
        this.configSpec = builder.build();
    }

    public String getFileName() {
        return "ExtraGeneral";
    }

    public String getTranslation() {
        return null;
    }

    public ModConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
