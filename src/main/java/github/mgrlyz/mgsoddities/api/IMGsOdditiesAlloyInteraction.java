package github.mgrlyz.mgsoddities.api;

import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IMGsOdditiesAlloyInteraction {
    <TIER extends IAdvanceTier> void onMGsOdditiesAlloyInteraction(Player player, ItemStack stack, @NotNull TIER tier);
}