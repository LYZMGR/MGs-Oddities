package github.mgrlyz.mgsoddities.attachments.containers.heat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.mgrlyz.mgsoddities.attachments.containers.IAttachedContainers;
import io.netty.buffer.ByteBuf;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

@NothingNullByDefault
public record AttachedHeat(
        List<HeatCapacitorData> containers) implements IAttachedContainers<HeatCapacitorData, AttachedHeat> {

    public static final AttachedHeat EMPTY = new AttachedHeat(Collections.emptyList());

    public static final Codec<AttachedHeat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HeatCapacitorData.CODEC.listOf().fieldOf(SerializationConstants.HEAT_CAPACITORS).forGetter(AttachedHeat::containers)
    ).apply(instance, AttachedHeat::new));
    public static final StreamCodec<ByteBuf, AttachedHeat> STREAM_CODEC = HeatCapacitorData.STREAM_CODEC.
            <List<HeatCapacitorData>>apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)).map(AttachedHeat::new, AttachedHeat::containers);

    public AttachedHeat {
        containers = Collections.unmodifiableList(containers);
    }

    @Override
    public HeatCapacitorData getEmptyStack() {
        throw new UnsupportedOperationException("Attached heat has no concept of a default stack and callers should override methods instead to use the proper default data");
    }

    @Override
    public AttachedHeat create(List<HeatCapacitorData> containers) {
        return new AttachedHeat(containers);
    }
}