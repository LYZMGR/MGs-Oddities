package github.mgrlyz.mgsoddities.common.config;

import github.mgrlyz.mgsoddities.MGsOddities;
import mekanism.common.config.IConfigTranslation;
import mekanism.common.config.TranslationPreset;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MGsOdditiesConfigTranslations implements IConfigTranslation {
    PARAGON_UNIVERSAL_CABLE_CAPACITY("tier.cable", "paragon", "Internal buffer in Joules of Paragon Universal Cable."),
    APOTHEOSIS_UNIVERSAL_CABLE_CAPACITY("tier.cable", "apotheosis", "Internal buffer in Joules of Apotheosis Universal Cable."),
    PARAGON_MECHANICAL_PIPE_CAPACITY("tier.pipe", "paragon", "Capacity of Paragon Mechanical Pipe in mb."),
    PARAGON_MECHANICAL_PIPE_PULL_AMOUNT("tier.pipe", "paragon", "Pump rate of Paragon Mechanical Pipe in mb."),
    APOTHEOSIS_MECHANICAL_PIPE_CAPACITY("tier.pipe", "apotheosis", "Capacity of Apotheosis Mechanical Pipe in mb."),
    APOTHEOSIS_MECHANICAL_PIPE_PULL_AMOUNT("tier.pipe", "apotheosis", "Pump rate of Apotheosis Mechanical Pipe in mb."),
    PARAGON_PRESSURIZED_TUBE_CAPACITY("tier.tube", "paragon", "Capacity of Paragon Pressurized Tube in mb."),
    PARAGON_PRESSURIZED_TUBE_PULL_AMOUNT("tier.tube", "paragon", "Pump rate of Paragon Pressurized Tube in mb."),
    APOTHEOSIS_PRESSURIZED_TUBE_CAPACITY("tier.tube", "apotheosis", "Capacity of Apotheosis Pressurized Tube in mb."),
    APOTHEOSIS_PRESSURIZED_TUBE_PULL_AMOUNT("tier.tube", "apotheosis", "Pump rate of Apotheosis Pressurized Tube in mb."),
    PARAGON_LOGISTICAL_TRANSPORTER_SPEED("tier.transporter", "paragon", "Five times the travel speed in m/s of Paragon Logistical Transporter."),
    PARAGON_LOGISTICAL_TRANSPORTER_PULL_AMOUNT("tier.transporter", "paragon", "Item throughput rate of Paragon Logistical Transporter in items/half second."),
    APOTHEOSIS_LOGISTICAL_TRANSPORTER_SPEED("tier.transporter", "apotheosis", "Five times the travel speed in m/s of Apotheosis Logistical Transporter."),
    APOTHEOSIS_LOGISTICAL_TRANSPORTER_PULL_AMOUNT("tier.transporter", "apotheosis", "Item throughput rate of Apotheosis Logistical Transporter in items/half second."),
    PARAGON_THERMODYNAMIC_CONDUCTOR_CONDUCTION("tier.conductorn", "paragon", "Conduction value of Paragon Thermodynamic Conductor."),
    PARAGON_THERMODYNAMIC_CONDUCTORN_CAPACITY("tier.conductorn", "paragon", "Heat capacity of Paragon Thermodynamic Conductor."),
    PARAGON_THERMODYNAMIC_CONDUCTORN_INSULATION("tier.conductorn", "paragon", "Insulation value of Paragon Thermodynamic Conductor."),
    APOTHEOSIS_THERMODYNAMIC_CONDUCTOR_CONDUCTION("tier.conductorn", "apotheosis", "Conduction value of Apotheosis Thermodynamic Conductor."),
    APOTHEOSIS_THERMODYNAMIC_CONDUCTORN_CAPACITY("tier.conductorn", "apotheosis", "Heat capacity of Apotheosis Thermodynamic Conductor."),
    APOTHEOSIS_THERMODYNAMIC_CONDUCTORN_INSULATION("tier.conductorn", "apotheosis", "Insulation value of Apotheosis Thermodynamic Conductor.");

    private final String key;
    private final String title;
    private final String tooltip;
    @Nullable
    private final String button;

    MGsOdditiesConfigTranslations(TranslationPreset preset, String type) {
        this(preset.path(type), preset.title(type), preset.tooltip(type));
    }

    MGsOdditiesConfigTranslations(TranslationPreset preset, String type, String tooltipSuffix) {
        this(preset.path(type), preset.title(type), preset.tooltip(type) + tooltipSuffix);
    }

    MGsOdditiesConfigTranslations(String path, String title, String tooltip) {
        this(path, title, tooltip, false);
    }

    MGsOdditiesConfigTranslations(String path, String title, String tooltip, boolean isSection) {
        this(path, title, tooltip, IConfigTranslation.getSectionTitle(title, isSection));
    }

    MGsOdditiesConfigTranslations(String path, String title, String tooltip, @Nullable String button) {
        this.key = Util.makeDescriptionId("configuration", MGsOddities.rl(path));
        this.title = title;
        this.tooltip = tooltip;
        this.button = button;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String tooltip() {
        return tooltip;
    }

    @Nullable
    @Override
    public String button() {
        return button;
    }
}