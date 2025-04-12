package github.mgrlyz.mgsoddities.common.recipe;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MGsOdditiesRecipe {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MGsOddities.MODID);

    public static final Supplier<RecipeType<NbtRecipe>> NBT_RECIPE =
            RECIPE_TYPES.register("nbt_recipe",() -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MGsOddities.MODID, "nbt_recipe")));
}
