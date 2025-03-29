package github.mgrlyz.mgsoddities.common.registries.item;

import github.mgrlyz.mgsoddities.api.IMGsOdditiesAlloyInteraction;
import github.mgrlyz.mgsoddities.api.tier.MGsOdditiesAlloyTier;
import github.mgrlyz.mgsoddities.common.capabilities.MGsOdditiesCapabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesItemAlloy extends Item {
    private final MGsOdditiesAlloyTier tier;

    public MGsOdditiesItemAlloy(MGsOdditiesAlloyTier tier, Item.Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && MekanismConfig.general.transmitterAlloyUpgrade.get()) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            IMGsOdditiesAlloyInteraction alloyInteraction = (IMGsOdditiesAlloyInteraction) WorldUtils.getCapability(world, MGsOdditiesCapabilities.MGSODDITIES_ALLOY_INTERACTION, pos, context.getClickedFace());
            if (alloyInteraction != null) {
                if (!world.isClientSide) {
                    alloyInteraction.onMGsOdditiesAlloyInteraction(player, context.getItemInHand(), this.tier);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    public MGsOdditiesAlloyTier getTier() {
        return this.tier;
    }
}