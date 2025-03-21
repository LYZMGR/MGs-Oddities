package github.mgrlyz.mgsoddities.api;

import github.mgrlyz.mgsoddities.api.tier.AlloyTier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IAlloyInteraction {
    void onAlloyInteraction(Player player, ItemStack stack, @NotNull AlloyTier tier);
}
