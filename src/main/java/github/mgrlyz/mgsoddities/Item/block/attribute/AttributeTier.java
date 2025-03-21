package github.mgrlyz.mgsoddities.Item.block.attribute;

import github.mgrlyz.mgsoddities.api.tier.ITier;
import github.mgrlyz.mgsoddities.block.attribute.Attribute;

public record AttributeTier<TIER extends ITier>(TIER tier) implements Attribute {
}
