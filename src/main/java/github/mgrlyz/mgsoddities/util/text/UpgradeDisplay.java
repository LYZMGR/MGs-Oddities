package github.mgrlyz.mgsoddities.util.text;

import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.api.Upgrade;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTextComponent;
import net.minecraft.network.chat.Component;

@NothingNullByDefault
public class UpgradeDisplay implements IHasTextComponent {

    private final Upgrade upgrade;
    private final int level;

    private UpgradeDisplay(Upgrade upgrade, int level) {
        this.upgrade = upgrade;
        this.level = level;
    }

    public static UpgradeDisplay of(Upgrade upgrade) {
        return of(upgrade, 0);
    }

    public static UpgradeDisplay of(Upgrade upgrade, int level) {
        return new UpgradeDisplay(upgrade, level);
    }

    @Override
    public Component getTextComponent() {
        if (upgrade.getMax() > 1 && level > 0) {
            return MGsOdditiesLang.UPGRADE_DISPLAY_LEVEL.translateColored(upgrade.getColor(), upgrade, EnumColor.GRAY, level);
        }
        return MGsOdditiesLang.GENERIC_LIST.translateColored(upgrade.getColor(), upgrade);
    }
}