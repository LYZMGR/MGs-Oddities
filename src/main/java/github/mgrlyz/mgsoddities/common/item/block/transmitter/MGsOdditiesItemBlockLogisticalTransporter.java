package github.mgrlyz.mgsoddities.common.item.block.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.TPTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityLogisticalTransporter;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.tier.TransporterTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MGsOdditiesItemBlockLogisticalTransporter extends MGsOdditiesItemBlockTransporter<MGsOdditiesTileEntityLogisticalTransporter> {
    public MGsOdditiesItemBlockLogisticalTransporter(BlockLargeTransmitter<MGsOdditiesTileEntityLogisticalTransporter> block, Item.Properties properties) {
        super(block, properties);
    }

    public @NotNull TransporterTier getTier() {
        return (TransporterTier) Objects.requireNonNull((TransporterTier) Attribute.getTier(this.getBlock(), TransporterTier.class));
    }

    protected void addStats(@NotNull ItemStack stack, @Nullable Item.@Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addStats(stack, context, tooltip, flag);
        TransporterTier tier = this.getTier();
        float tickRate = Math.max(context.tickRate(), 1.0F);
        float speed = (float) TPTier.getSpeed(tier) / (100.0F / tickRate);
        float pull = (float)TPTier.getPullAmount(tier) * tickRate / 10.0F;
        tooltip.add(MekanismLang.SPEED.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, speed}));
        tooltip.add(MekanismLang.PUMP_RATE.translateColored(EnumColor.INDIGO, new Object[]{EnumColor.GRAY, pull}));
    }
}