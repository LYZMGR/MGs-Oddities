package github.mgrlyz.mgsoddities.common.block.attribute;

import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import mekanism.common.MekanismLang;
import mekanism.common.content.blocktype.BlockType;

import java.util.HashMap;
import java.util.Map;

public record MGsOdditiesAttributeTier<TIER extends IAdvanceTier>(TIER tier) implements MGsOdditiesAttribute{
    private static final Map<IAdvanceTier, BlockType> typeCache = new HashMap<>();

    public MGsOdditiesAttributeTier(TIER tier) {
        this.tier = tier;
    }

    public static <T extends IAdvanceTier> BlockType getPassthroughType(T tier) {
        return typeCache.computeIfAbsent(tier, t -> BlockType.BlockTypeBuilder.createBlock(MekanismLang.EMPTY).with(new MGsOdditiesAttribute[]{new MGsOdditiesAttributeTier<>(t)}).build());
    }
}