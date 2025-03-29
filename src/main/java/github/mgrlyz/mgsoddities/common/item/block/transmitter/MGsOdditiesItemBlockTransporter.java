package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityLogisticalTransporterBase;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MGsOdditiesItemBlockTransporter<TILE extends MGsOdditiesTileEntityLogisticalTransporterBase> extends ItemBlockTooltip<BlockLargeTransmitter<TILE>> {
    private final @Nullable ILangEntry mgsodditiesDetails;

    public MGsOdditiesItemBlockTransporter(BlockLargeTransmitter<TILE> block, Item.Properties properties) {
        this(block, properties, (ILangEntry)null);
    }

    public MGsOdditiesItemBlockTransporter(BlockLargeTransmitter<TILE> block, Item.Properties properties, @Nullable ILangEntry mgsodditiesDetails) {
        super(block, true, properties);
        this.mgsodditiesDetails = mgsodditiesDetails;
    }

    protected void addDetails(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.CAPABLE_OF_TRANSFERRING.translateColored(EnumColor.DARK_GRAY));
        tooltip.add(MekanismLang.ITEMS.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.UNIVERSAL}));
        tooltip.add(MekanismLang.BLOCKS.translateColored(EnumColor.PURPLE, new Object[]{MekanismLang.UNIVERSAL}));
        if (this.mgsodditiesDetails != null) {
            tooltip.add(this.mgsodditiesDetails.translateColored(EnumColor.DARK_RED));
        }

    }
}