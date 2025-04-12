package github.mgrlyz.mgsoddities.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class NbtRecipeSerializer implements RecipeSerializer<NbtRecipe> {
    public static final MapCodec<NbtRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(NbtRecipe::getInputItems),
            ItemStack.CODEC.fieldOf("result").forGetter(NbtRecipe::getOutput)
    ).apply(inst, ((ingredients, result) -> new NbtRecipe(NonNullList.of(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0])), result))));

    public static final StreamCodec<RegistryFriendlyByteBuf, NbtRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    NbtRecipe::getInputItems,

                    ItemStack.STREAM_CODEC,
                    NbtRecipe::getOutput,

                    (inputItems, result) -> new NbtRecipe(
                            NonNullList.of(Ingredient.EMPTY, inputItems.toArray(new Ingredient[0])),
                            result
                    )
            );

    @Override
    public MapCodec<NbtRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, NbtRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
