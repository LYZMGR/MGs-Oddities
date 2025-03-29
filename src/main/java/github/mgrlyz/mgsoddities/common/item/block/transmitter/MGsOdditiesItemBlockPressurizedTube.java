package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.TTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityPressurizedTube;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockSmallTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.tier.TubeTier;
import mekanism.common.util.text.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MGsOdditiesItemBlockPressurizedTube extends ItemBlockTooltip<BlockSmallTransmitter<MGsOdditiesTileEntityPressurizedTube>> {
    public MGsOdditiesItemBlockPressurizedTube(BlockSmallTransmitter<MGsOdditiesTileEntityPressurizedTube> block, Item.Properties properties) {
        super(block, true, properties);
    }

    public @NotNull TubeTier getTier() {
        return (TubeTier) Objects.requireNonNull((TubeTier) Attribute.getTier(this.getBlock(), TubeTier.class));
    }

    protected void addDetails(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MekanismLang.CHEMICAL.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.MEKANISM}));
    }

    protected void addStats(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        TubeTier tier = this.getTier();
        tooltip.add(MekanismLang.CAPACITY_MB_PER_TICK.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TextUtils.format(TTier.getTubeCapacity(tier))}));
        tooltip.add(MekanismLang.PUMP_RATE_MB.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TextUtils.format(TTier.getTubePullAmount(tier))}));
    }
}
