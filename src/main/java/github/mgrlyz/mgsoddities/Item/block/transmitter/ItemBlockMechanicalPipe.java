package github.mgrlyz.mgsoddities.Item.block.transmitter;

import github.mgrlyz.mgsoddities.Item.block.ItemBlockTooltip;
import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.block.attribute.Attribute;
import github.mgrlyz.mgsoddities.tier.PipeTier;
import github.mgrlyz.mgsoddities.tile.transmitter.BlockLargeTransmitter;
import github.mgrlyz.mgsoddities.tile.transmitter.TileEntityMechanicalPipe;
import github.mgrlyz.mgsoddities.util.text.TextUtils;
import mekanism.api.text.EnumColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBlockMechanicalPipe extends ItemBlockTooltip<BlockLargeTransmitter<TileEntityMechanicalPipe>> {

    public ItemBlockMechanicalPipe(BlockLargeTransmitter<TileEntityMechanicalPipe> block, Item.Properties properties) {
        super(block, true, properties);
    }

    @Override
    public PipeTier getTier() { return Attribute.getTier(getBlock(), PipeTier.class);}

    @Override
    protected void addDetails(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MGsOdditiesLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MGsOdditiesLang.FLUIDS.translateColored(EnumColor.PURPLE, EnumColor.GRAY, MGsOdditiesLang.FORGE));
    }

    @Override
    protected void addStats(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        PipeTier tier = getTier();
        tooltip.add(MGsOdditiesLang.CAPACITY_MB_PER_TICK.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getPipeCapacity())));
        tooltip.add(MGsOdditiesLang.PUMP_RATE_MB.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getPipePullAmount())));
    }
}