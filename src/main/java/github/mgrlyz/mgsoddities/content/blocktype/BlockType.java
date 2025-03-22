package github.mgrlyz.mgsoddities.content.blocktype;

import github.mgrlyz.mgsoddities.api.tier.ITier;
import github.mgrlyz.mgsoddities.block.interfaces.ITypeBlock;
import github.mgrlyz.mgsoddities.block.attribute.*;
import mekanism.api.text.ILangEntry;
import mekanism.common.lib.transmitter.TransmissionType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockType {

    private final ILangEntry description;

    private final Map<Class<? extends Attribute>, Attribute> attributeMap = new HashMap<>();

    public BlockType(ILangEntry description) {
        this.description = description;
    }

    public boolean has(Class<? extends Attribute> type) {
        return attributeMap.containsKey(type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <ATTRIBUTE extends Attribute> ATTRIBUTE get(Class<ATTRIBUTE> type) {
        return (ATTRIBUTE) attributeMap.get(type);
    }

    @SafeVarargs
    protected final void setFrom(BlockTypeTile<?> tile, Class<? extends Attribute>... types) {
        for (Class<? extends Attribute> type : types) {
            attributeMap.put(type, tile.get(type));
        }
    }

    public void add(Attribute... attrs) {
        for (Attribute attr : attrs) {
            attributeMap.put(attr.getClass(), attr);
        }
    }

    @SafeVarargs
    public final void remove(Class<? extends Attribute>... attrs) {
        for (Class<? extends Attribute> attr : attrs) {
            attributeMap.remove(attr);
        }
    }

    public Collection<Attribute> getAll() {
        return attributeMap.values();
    }

    @NotNull
    public ILangEntry getDescription() {
        return description;
    }

    public static boolean is(Block block, BlockType... types) {
        if (block instanceof ITypeBlock typeBlock) {
            for (BlockType type : types) {
                if (typeBlock.getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BlockType get(Block block) {
        return block instanceof ITypeBlock typeBlock ? typeBlock.getType() : null;
    }

    public static class BlockTypeBuilder<BLOCK extends BlockType, T extends BlockTypeBuilder<BLOCK, T>> {

        protected final BLOCK holder;

        protected BlockTypeBuilder(BLOCK holder) {
            this.holder = holder;
        }

        public static BlockType.BlockTypeBuilder<BlockType, ?> createBlock(ILangEntry description) {
            return new BlockType.BlockTypeBuilder<>(new BlockType(description));
        }

        @SuppressWarnings("unchecked")
        public T self() {
            return (T) this;
        }
        public final T replace(Attribute... attrs) {
            return with(attrs);
        }

        public final T with(Attribute... attrs) {
            holder.add(attrs);
            return self();
        }

        public final T withBounding(AttributeHasBounding.HandleBoundingBlock boundingPosHandlers) {
            return with(new AttributeHasBounding(boundingPosHandlers));
        }

        @SafeVarargs
        public final T without(Class<? extends Attribute>... attrs) {
            holder.remove(attrs);
            return self();
        }

        public final T withSideConfig(TransmissionType... types) {
            return with(AttributeSideConfig.create(types));
        }

        public T withCustomShape(VoxelShape[] shape) {
            return with(new AttributeCustomShape(shape));
        }

        public T withLight(int light) {
            return with(new Attributes.AttributeLight(light));
        }

        public T withComputerSupport(String name) {
            return with(new Attributes.AttributeComputerIntegration(name));
        }

        public T withComputerSupport(ITier tier, String name) {
            return withComputerSupport(tier.getBaseTier().getLowerName() + name);
        }

        public final T externalMultiblock() {
            return with(AttributeMultiblock.EXTERNAL, Attributes.AttributeMobSpawn.WHEN_NOT_FORMED, Attributes.AttributeCustomPathType.WHEN_NOT_FORMED);
        }

        public final T internalMultiblock() {
            return with(AttributeMultiblock.INTERNAL, Attributes.AttributeMobSpawn.WHEN_NOT_FORMED, Attributes.AttributeCustomPathType.WHEN_NOT_FORMED);
        }

        public BLOCK build() {
            return holder;
        }
    }
}