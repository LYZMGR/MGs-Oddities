package github.mgrlyz.mgsoddities;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.world.entity.EquipmentSlot;

@NothingNullByDefault
public enum MGsOdditiesLang implements ILangEntry {
    //Gui lang strings
    MGSODDITIES("constants","mod_name"),
    //Equipment
    HEAD("equipment", "head"),
    BODY("equipment", "body"),
    LEGS("equipment", "legs"),
    FEET("equipment", "feet"),
    MAINHAND("equipment", "mainhand"),
    OFFHAND("equipment", "offhand"),
    //Descriptions
    DESCRIPTION_CABLE("description", "cable"),
    DESCRIPTION_PIPE("description", "pipe"),
    DESCRIPTION_TUBE("description", "tube"),
    DESCRIPTION_TRANSPORTER("description", "transporter"),
    DESCRIPTION_CONDUCTOR("description", "conductor"),
    //Transmitter tooltips
    CAPABLE_OF_TRANSFERRING("transmitter", "capable_of_transferring"),
    FLUIDS("transmitter", "fluids"),
    CAPACITY_MB_PER_TICK("capacity", "mb.per_tick"),
    PUMP_RATE_MB("transmitter", "pump_rate.mb"),
    //Gui lang strings
    FORGE("constants", "forge"),
    //Hold for
    HOLD_FOR_DETAILS("tooltip", "hold_for_details"),
    HOLD_FOR_DESCRIPTION("tooltip", "hold_for_description"),
    //Generic
    GENERIC_PERCENT("generic", "percent"),
    GENERIC_STORED_MB("generic", "stored.mb"),
    GENERIC_LIST("generic", "list"),
    GENERIC_HOURS_MINUTES("generic", "hours_minutes"),
    GENERIC_MINUTES("generic", "minutes"),
    //Tooltip stuff
    HAS_INVENTORY("tooltip", "inventory"),
    //Upgrades
    UPGRADE_DISPLAY_LEVEL("upgrade", "display.level");


    private final String key;

    MGsOdditiesLang(String type, String path) {
        this(Util.makeDescriptionId(type, MGsOddities.rl(path)));
    }

    MGsOdditiesLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

    public static ILangEntry get(EquipmentSlot type) {
        return switch (type) {
            case HEAD -> HEAD;
            case CHEST, BODY -> BODY;
            case LEGS -> LEGS;
            case FEET -> FEET;
            case MAINHAND -> MAINHAND;
            case OFFHAND -> OFFHAND;
        };
    }
}