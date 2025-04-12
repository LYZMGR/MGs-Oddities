package github.mgrlyz.mgsoddities.common.recipe;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MGsOdditiesRecipeSerializer {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MGsOddities.MODID);

    public static final Supplier<RecipeSerializer<NbtRecipe>> NBT_RECIPE_SERIALIZER =
            RECIPE_SERIALIZER.register("nbt_recipe", NbtRecipeSerializer::new);
}
