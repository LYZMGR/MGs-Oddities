package github.mgrlyz.mgsoddities.api.providers;

import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.TextComponentUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@MethodsReturnNonnullByDefault
public interface IBaseProvider extends IHasTextComponent, IHasTranslationKey {
    ResourceLocation getRegistryName();
    default String getName() {
        return getRegistryName().getPath();
    }

    @Override
    default Component getTextComponent() {
        return TextComponentUtil.translate(getTranslationKey());
    }
}