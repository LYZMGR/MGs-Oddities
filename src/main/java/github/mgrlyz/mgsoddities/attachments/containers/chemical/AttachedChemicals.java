package github.mgrlyz.mgsoddities.attachments.containers.chemical;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.mgrlyz.mgsoddities.attachments.containers.IAttachedContainers;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

@NothingNullByDefault
public record AttachedChemicals(
        List<ChemicalStack> containers) implements IAttachedContainers<ChemicalStack, AttachedChemicals> {

    public static final AttachedChemicals EMPTY = new AttachedChemicals(Collections.emptyList());

    public static final Codec<AttachedChemicals> ACTUAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStack.LENIENT_OPTIONAL_CODEC.listOf().fieldOf(SerializationConstants.CHEMICAL_TANKS).forGetter(AttachedChemicals::containers)
    ).apply(instance, AttachedChemicals::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AttachedChemicals> STREAM_CODEC =
            ChemicalStack.OPTIONAL_STREAM_CODEC.<List<ChemicalStack>>apply(ByteBufCodecs.collection(NonNullList::createWithCapacity))
                    .map(AttachedChemicals::new, AttachedChemicals::containers);

    @Deprecated(forRemoval = true)
    public static final Codec<AttachedChemicals> GAS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStack.LENIENT_OPTIONAL_CODEC.listOf().fieldOf(SerializationConstants.GAS_TANKS).forGetter(AttachedChemicals::containers)
    ).apply(instance, AttachedChemicals::new));
    @Deprecated(forRemoval = true)
    public static final Codec<AttachedChemicals> INFUSE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStack.LENIENT_OPTIONAL_CODEC.listOf().fieldOf(SerializationConstants.INFUSION_TANKS).forGetter(AttachedChemicals::containers)
    ).apply(instance, AttachedChemicals::new));
    @Deprecated(forRemoval = true)
    public static final Codec<AttachedChemicals> PIGMENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStack.LENIENT_OPTIONAL_CODEC.listOf().fieldOf(SerializationConstants.PIGMENT_TANKS).forGetter(AttachedChemicals::containers)
    ).apply(instance, AttachedChemicals::new));
    @Deprecated(forRemoval = true)
    public static final Codec<AttachedChemicals> SLURRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStack.LENIENT_OPTIONAL_CODEC.listOf().fieldOf(SerializationConstants.SLURRY_TANKS).forGetter(AttachedChemicals::containers)
    ).apply(instance, AttachedChemicals::new));
    public static final Codec<AttachedChemicals> CODEC = Codec.withAlternative(ACTUAL_CODEC, Codec.withAlternative(GAS_CODEC, Codec.withAlternative(INFUSE_CODEC, Codec.withAlternative(PIGMENT_CODEC, SLURRY_CODEC))));

    public static AttachedChemicals create(int containers) {
        return new AttachedChemicals(NonNullList.withSize(containers, ChemicalStack.EMPTY));
    }

    public AttachedChemicals {
        containers = Collections.unmodifiableList(containers);
    }

    @Override
    public ChemicalStack getEmptyStack() {
        return ChemicalStack.EMPTY;
    }

    @Override
    public AttachedChemicals create(List<ChemicalStack> containers) {
        return new AttachedChemicals(containers);
    }
}