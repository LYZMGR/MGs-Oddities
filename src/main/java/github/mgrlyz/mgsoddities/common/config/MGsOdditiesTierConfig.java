package github.mgrlyz.mgsoddities.common.config;

import github.mgrlyz.mgsoddities.common.tier.ECtier;
import github.mgrlyz.mgsoddities.common.util.MGsOdditiesEnumUtils;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedLongValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Locale;

public class MGsOdditiesTierConfig extends BaseMekanismConfig {
    private final ModConfigSpec configSpec;
    public final CachedLongValue paragonUniversalCableCapacity;
    public final CachedLongValue apotheosisUniversalCableCapacity;
    public final CachedLongValue paragonMechanicalPipeCapacity;
    public final CachedLongValue paragonMechanicalPipePullAmount;
    public final CachedLongValue apotheosisMechanicalPipeCapacity;
    public final CachedLongValue apotheosisMechanicalPipePullAmount;
    public final CachedLongValue paragonPressurizedTubeCapacity;
    public final CachedLongValue paragonPressurizedTubePullAmount;
    public final CachedLongValue apotheosisPressurizedTubeCapacity;
    public final CachedLongValue apotheosisPressurizedTubePullAmount;
    public final CachedLongValue paragonLogisticalTransporterSpeed;
    public final CachedLongValue paragonLogisticalTransporterPullAmount;
    public final CachedLongValue apotheosisLogisticalTransporterSpeed;
    public final CachedLongValue apotheosisLogisticalTransporterPullAmount;
    public final CachedLongValue paragonThermodynamicConductorConduction;
    public final CachedLongValue paragonThermodynamicConductornCapacity;
    public final CachedLongValue paragonThermodynamicConductornInsulation;
    public final CachedLongValue apotheosisThermodynamicConductorConduction;
    public final CachedLongValue apotheosisThermodynamicConductornCapacity;
    public final CachedLongValue apotheosisThermodynamicConductornInsulation;

    public MGsOdditiesTierConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Tier Config. This config is synced from server to client.").push("tier");
        this.addStoragesCategory(builder);
        builder.comment("Transmitters").push("transmitters");
        builder.comment("Universal Cables").push("energy");
        this.paragonUniversalCableCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_UNIVERSAL_CABLE_CAPACITY,"paragonUniversalCable",65_536_000L, 1L,Long.MAX_VALUE);
        this.apotheosisUniversalCableCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_UNIVERSAL_CABLE_CAPACITY, "apotheosisUniversalCable", 524_288_000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.comment("Mechanical Pipes").push("fluid");
        this.paragonMechanicalPipeCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_MECHANICAL_PIPE_CAPACITY, "paragonMechanicalPipesCapacity", 512_000L, 1L, Long.MAX_VALUE);
        this.paragonMechanicalPipePullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_MECHANICAL_PIPE_PULL_AMOUNT, "paragonMechanicalPipesPullAmount", 128_000L, 1L, 2_147_483_647L);
        this.apotheosisMechanicalPipeCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_MECHANICAL_PIPE_CAPACITY, "apotheosisMechanicalPipesCapacity", 2048_000L, 1L, Long.MAX_VALUE);
        this.apotheosisMechanicalPipePullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_MECHANICAL_PIPE_PULL_AMOUNT, "apotheosisMechanicalPipesPullAmount", 512_000L, 1L, 2_147_483_647L);
        builder.pop();
        builder.comment("Pressurized Tubes").push("chemical");
        this.paragonPressurizedTubeCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_PRESSURIZED_TUBE_CAPACITY, "paragonPressurizedTubesCapacity", 4096_000L, 1L, Long.MAX_VALUE);
        this.paragonPressurizedTubePullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_PRESSURIZED_TUBE_PULL_AMOUNT, "paragonPressurizedTubesPullAmount", 1024_000L, 1L, Long.MAX_VALUE);
        this.apotheosisPressurizedTubeCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_PRESSURIZED_TUBE_CAPACITY, "apotheosisPressurizedTubesCapacity", 16_384_000L, 1L, Long.MAX_VALUE);
        this.apotheosisPressurizedTubePullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_PRESSURIZED_TUBE_PULL_AMOUNT, "apotheosisPressurizedTubesPullAmount", 4_096_000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.comment("Logistical Transporters").push("item");
        this.paragonLogisticalTransporterSpeed = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_LOGISTICAL_TRANSPORTER_SPEED, "paragonLogisticalTransporterSpeed", 55L, 1L, 2_147_483_647L);
        this.paragonLogisticalTransporterPullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_LOGISTICAL_TRANSPORTER_PULL_AMOUNT, "paragonLogisticalTransporterPullAmount", 128L, 1L, 2_147_483_647L);
        this.apotheosisLogisticalTransporterSpeed = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_LOGISTICAL_TRANSPORTER_SPEED, "apotheosisLogisticalTransporterSpeed", 60L, 1L, 2_147_483_647L);
        this.apotheosisLogisticalTransporterPullAmount = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_LOGISTICAL_TRANSPORTER_PULL_AMOUNT, "apotheosisLogisticalTransporterPullAmount", 256L, 1L, 2_147_483_647L);
        builder.pop();
        builder.comment("Thermodynamic Conductors").push("heat");
        this.paragonThermodynamicConductorConduction = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_THERMODYNAMIC_CONDUCTOR_CONDUCTION, "paragonThermodynamicConductorConduction", 10L, 1L, Long.MAX_VALUE);
        this.paragonThermodynamicConductornCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_THERMODYNAMIC_CONDUCTORN_CAPACITY, "paragonThermodynamicConductornCapacity", 1L, 1L, Long.MAX_VALUE);
        this.paragonThermodynamicConductornInsulation = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.PARAGON_THERMODYNAMIC_CONDUCTORN_INSULATION, "paragonThermodynamicConductornInsulation", 400_000L, 1L, Long.MAX_VALUE);
        this.apotheosisThermodynamicConductorConduction = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR_CONDUCTION, "apotheosisThermodynamicConductorConduction", 15L, 1L, Long.MAX_VALUE);
        this.apotheosisThermodynamicConductornCapacity = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_THERMODYNAMIC_CONDUCTORN_CAPACITY, "apotheosisThermodynamicConductornCapacity", 1L, 1L, Long.MAX_VALUE);
        this.apotheosisThermodynamicConductornInsulation = CachedLongValue.define(this, builder, MGsOdditiesConfigTranslations.APOTHEOSIS_THERMODYNAMIC_CONDUCTORN_INSULATION, "apotheosisThermodynamicConductornInsulation", 800_000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.pop();
        builder.pop();
        this.configSpec = builder.build();
    }

    private void addStoragesCategory(ModConfigSpec.Builder builder) {
        builder.comment("Storages").push("Storages");
        this.addEnergyCubeCategory(builder);
        builder.pop();
    }

    private void addEnergyCubeCategory(ModConfigSpec.Builder builder) {
        builder.comment("Energy Cubes").push("energy_cubes");

        for(ECtier tier : MGsOdditiesEnumUtils.ENERGY_CUBE_TIERS) {
            String tierName = tier.getAdvanceTier().getSimpleName();
            CachedLongValue storageReference = CachedLongValue.wrap(this, builder.comment("Maximum number of Joules " + tierName + " energy cubes can store.").defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getAdvanceMaxEnergy(), 1L, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, builder.comment("Output rate in Joules of " + tierName + " energy cubes.").defineInRange(tierName.toLowerCase(Locale.ROOT) + "Output", tier.getAdvanceOutput(), 1L, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }

        builder.pop();
    }
    
    public String getFileName() {
        return "TierConfig";
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