package github.mgrlyz.mgsoddities.block.attribute;

import github.mgrlyz.mgsoddities.api.tier.ITier;

public record AttributeTier<TIER extends ITier>(TIER tier) implements Attribute {
}