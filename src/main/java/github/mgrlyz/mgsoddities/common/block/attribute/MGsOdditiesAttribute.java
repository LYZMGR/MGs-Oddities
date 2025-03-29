package github.mgrlyz.mgsoddities.common.block.attribute;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import mekanism.common.block.attribute.Attribute;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface MGsOdditiesAttribute extends Attribute {

    @Nullable
    static <TIER extends IAdvanceTier> TIER getAdvanceTier(Holder<Block> block, Class<TIER> tierClass) {
        return getAdvanceTier(block.value(), tierClass);
    }

    @Nullable
    static <TIER extends IAdvanceTier> TIER getAdvanceTier(Block block, Class<TIER> tierClass) {
        MGsOdditiesAttributeTier<?> attr = Attribute.get(block, MGsOdditiesAttributeTier.class);
        return attr == null ? null : tierClass.cast(attr.tier());
    }

    @Nullable
    static AdvanceTier getAdvanceTier(Block block) {
        MGsOdditiesAttributeTier<?> attr = Attribute.get(block, MGsOdditiesAttributeTier.class);
        return attr == null ? null : attr.tier().getAdvanceTier();
    }
}