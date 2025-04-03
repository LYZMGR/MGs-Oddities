package github.mgrlyz.mgsoddities.common;

import github.mgrlyz.mgsoddities.MGsOddities;
import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;

public enum MGsOdditiesLang implements ILangEntry {
    MGSODDITIES("constants", "mod_name");
    private final String key;

    MGsOdditiesLang(String type, String path) {
        this(Util.makeDescriptionId(type, MGsOddities.rl(path)));
    }

    MGsOdditiesLang(String key) {
        this.key = key;
    }

    @Override
    public @NotNull String getTranslationKey() {
        return key;
    }
}