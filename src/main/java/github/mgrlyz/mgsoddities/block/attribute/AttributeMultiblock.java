package github.mgrlyz.mgsoddities.block.attribute;

import mekanism.common.lib.multiblock.*;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class AttributeMultiblock implements Attribute {

    public static final Attribute EXTERNAL = new AttributeMultiblock(github.mgrlyz.mgsoddities.block.attribute.AttributeMultiblock.MultiblockType.EXTERNAL);
    public static final Attribute STRUCTURAL = new AttributeMultiblock(github.mgrlyz.mgsoddities.block.attribute.AttributeMultiblock.MultiblockType.STRUCTURAL);
    public static final Attribute INTERNAL = new AttributeMultiblock(github.mgrlyz.mgsoddities.block.attribute.AttributeMultiblock.MultiblockType.INTERNAL);

    private final AttributeMultiblock.MultiblockType type;

    private AttributeMultiblock(AttributeMultiblock.MultiblockType type) {
        this.type = type;
    }

    @Nullable
    public MultiblockData getMultiblock(Level level, BlockPos pos, UUID uuid) {
        BlockEntity tile = WorldUtils.getTileEntity(level, pos);
        switch (type) {
            case EXTERNAL -> {
                if (tile instanceof IMultiblock<?> multiblockTile) {
                    MultiblockData multiblock = multiblockTile.getMultiblock();
                    if (Objects.equals(multiblock.inventoryID, uuid)) {
                        return multiblock;
                    }
                }
            }
            case STRUCTURAL -> {
                if (tile instanceof IStructuralMultiblock structuralMultiblock && structuralMultiblock.hasFormedMultiblock()) {
                    for (Structure structure : structuralMultiblock.getStructureMap().values()) {
                        MultiblockData data = structure.getMultiblockData();
                        if (data != null && data.isFormed() && Objects.equals(data.inventoryID, uuid)) {
                            return data;
                        }
                    }
                }
            }
            case INTERNAL -> {
                if (tile instanceof IInternalMultiblock internal && Objects.equals(internal.getMultiblockUUID(), uuid)) {
                    return internal.getMultiblock();
                }
            }
        }
        return null;
    }

    private enum MultiblockType {
        EXTERNAL,
        STRUCTURAL,
        INTERNAL
    }
}