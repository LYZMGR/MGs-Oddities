package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.TCTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityThermodynamicConductor;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockSmallTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.tier.ConductorTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MGsOdditiesItemBlockThermodynamicConductor extends ItemBlockTooltip<BlockSmallTransmitter<MGsOdditiesTileEntityThermodynamicConductor>> {
    public MGsOdditiesItemBlockThermodynamicConductor(BlockSmallTransmitter<MGsOdditiesTileEntityThermodynamicConductor> block, Item.Properties properties) {
        super(block, true, properties);
    }

    public @NotNull ConductorTier getTier() {
        return (ConductorTier) Objects.requireNonNull((ConductorTier) Attribute.getTier(this.getBlock(), ConductorTier.class));
    }

    protected void addDetails(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MekanismLang.HEAT.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.MEKANISM}));
    }

    protected void addStats(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        ConductorTier tier = this.getTier();
        tooltip.add(MekanismLang.CONDUCTION.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TCTier.getConduction(tier)}));
        tooltip.add(MekanismLang.INSULATION.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TCTier.getConductionInsulation(tier)}));
        tooltip.add(MekanismLang.HEAT_CAPACITY.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TCTier.getHeatCapacity(tier)}));
    }
}