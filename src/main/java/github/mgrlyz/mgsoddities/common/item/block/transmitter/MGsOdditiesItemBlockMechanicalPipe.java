package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.PTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityMechanicalPipe;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.tier.PipeTier;
import mekanism.common.util.text.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MGsOdditiesItemBlockMechanicalPipe extends ItemBlockTooltip<BlockLargeTransmitter<MGsOdditiesTileEntityMechanicalPipe>> {
    public MGsOdditiesItemBlockMechanicalPipe(BlockLargeTransmitter<MGsOdditiesTileEntityMechanicalPipe> block, Item.Properties properties) {
        super(block, true, properties);
    }

    public @NotNull PipeTier getTier() {
        return (PipeTier) Objects.requireNonNull((PipeTier) Attribute.getTier(this.getBlock(), PipeTier.class));
    }

    protected void addDetails(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MekanismLang.FLUIDS.translateColored(EnumColor.PURPLE, new Object[]{EnumColor.GRAY, MekanismLang.FORGE}));
    }

    protected void addStats(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        PipeTier tier = this.getTier();
        tooltip.add(MekanismLang.CAPACITY_MB_PER_TICK.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TextUtils.format(PTier.getPipeCapacity(tier))}));
        tooltip.add(MekanismLang.PUMP_RATE_MB.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, TextUtils.format((long)PTier.getPipePullAmount(tier))}));
    }
}