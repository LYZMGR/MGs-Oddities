package github.mgrlyz.mgsoddities.registration;

import net.minecraft.resources.ResourceLocation;

public interface INamedEntry {
    default String getName() {
        return getId().getPath();
    }

    ResourceLocation getId();
}
