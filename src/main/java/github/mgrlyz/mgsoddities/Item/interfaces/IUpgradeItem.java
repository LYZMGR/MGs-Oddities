package github.mgrlyz.mgsoddities.Item.interfaces;

import github.mgrlyz.mgsoddities.api.Upgrade;
import net.minecraft.world.item.ItemStack;

public interface IUpgradeItem {

    Upgrade getUpgradeType(ItemStack stack);
}