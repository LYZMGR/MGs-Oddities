package github.mgrlyz.mgsoddities.block.attribute;

import github.mgrlyz.mgsoddities.api.Upgrade;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record AttributeUpgradeSupport(@NotNull Set<Upgrade> supportedUpgrades) implements Attribute {

    public static final AttributeUpgradeSupport DEFAULT_MACHINE_UPGRADES = AttributeUpgradeSupport.create(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING);
    public static final AttributeUpgradeSupport DEFAULT_ADVANCED_MACHINE_UPGRADES = AttributeUpgradeSupport.create(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING, Upgrade.CHEMICAL);
    public static final AttributeUpgradeSupport SPEED_ENERGY = AttributeUpgradeSupport.create(Upgrade.SPEED, Upgrade.ENERGY);
    public static final AttributeUpgradeSupport MUFFLING_ONLY = AttributeUpgradeSupport.create(Upgrade.MUFFLING);
    public static final AttributeUpgradeSupport ENERGY_ONLY = AttributeUpgradeSupport.create(Upgrade.ENERGY);
    public static final AttributeUpgradeSupport SPEED_ONLY = AttributeUpgradeSupport.create(Upgrade.SPEED);
    public static final AttributeUpgradeSupport ANCHOR_ONLY = AttributeUpgradeSupport.create(Upgrade.ANCHOR);

    public static AttributeUpgradeSupport create(Upgrade... supportedUpgrades) {
        if (supportedUpgrades.length == 0) {
            throw new IllegalArgumentException("There must be at least one upgrade that is supported");
        }
        Set<Upgrade> upgrades;
        if (supportedUpgrades.length == 1) {
            upgrades = Set.of(supportedUpgrades[0]);
        } else if (supportedUpgrades.length == 2) {
            upgrades = Set.of(supportedUpgrades[0], supportedUpgrades[1]);
        } else {
            upgrades = EnumSet.noneOf(Upgrade.class);
            Collections.addAll(upgrades, supportedUpgrades);
            upgrades = Collections.unmodifiableSet(upgrades);
        }
        return new AttributeUpgradeSupport(upgrades);
    }
}
