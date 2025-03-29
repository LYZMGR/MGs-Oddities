package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.CTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityUniversalCable;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockSmallTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.tier.CableTier;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MGsOdditiesItemBlockUniversalCable extends ItemBlockTooltip<BlockSmallTransmitter<MGsOdditiesTileEntityUniversalCable>> {
    public MGsOdditiesItemBlockUniversalCable(BlockSmallTransmitter<MGsOdditiesTileEntityUniversalCable> block, Item.Properties properties) {
        super(block, true, properties);
    }

    public @NotNull CableTier getTier() {
        return (CableTier) Objects.requireNonNull((CableTier) Attribute.getTier(this.getBlock(), CableTier.class));
    }

    protected void addDetails(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MekanismLang.GENERIC_TRANSFER.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.ENERGY_FORGE_SHORT, MekanismLang.FORGE}));
        tooltip.add(MekanismLang.GENERIC_TRANSFER.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.ENERGY_JOULES_PLURAL, MekanismLang.MEKANISM}));
    }

    protected void addStats(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        CableTier tier = this.getTier();
        tooltip.add(MekanismLang.CAPACITY_PER_TICK.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, EnergyDisplay.of(CTier.getCapacityAsLong(tier))}));
    }
}
