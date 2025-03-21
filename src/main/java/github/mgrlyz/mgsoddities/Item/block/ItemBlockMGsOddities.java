package github.mgrlyz.mgsoddities.Item.block;

import github.mgrlyz.mgsoddities.api.providers.IBlockProvider;
import github.mgrlyz.mgsoddities.api.tier.ITier;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockMGsOddities<BLOCK extends Block> extends BlockItem implements IBlockProvider {

    public ItemBlockMGsOddities(@NotNull BLOCK block, Item.Properties properties) {
        super(block, properties);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public BLOCK getBlock() {
        return (BLOCK) super.getBlock();
    }

    public ITier getTier() {
        return null;
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        if (getBlock() instanceof IColoredBlock coloredBlock) {
            return TextComponentUtil.build(coloredBlock.getColor(), super.getName(stack));
        }
        ITier tier = getTier();
        if (tier == null) {
            return super.getName(stack);
        }
        return TextComponentUtil.build(tier.getBaseTier().getColor(), super.getName(stack));
    }
}