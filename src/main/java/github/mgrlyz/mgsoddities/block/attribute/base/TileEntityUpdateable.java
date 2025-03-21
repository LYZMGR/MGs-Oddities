package github.mgrlyz.mgsoddities.block.attribute.base;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.network.to_client.PacketUpdateTile;
import mekanism.api.Chunk3D;
import mekanism.common.network.PacketUtils;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.interfaces.ITileWrapper;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TileEntityUpdateable extends BlockEntity implements ITileWrapper {

    @Nullable
    private GlobalPos cachedCoord;
    private boolean cacheCoord;
    private long lastSave;
    private final long worldPositionLong;

    public TileEntityUpdateable(TileEntityTypeRegistryObject<?> type, BlockPos pos, BlockState state) {
        super(type.get(), pos, state);
        this.worldPositionLong = pos.asLong();
    }

    public List<DataComponentType<?>> getRemapEntries() {
        return new ArrayList<>(collectComponents().keySet());
    }

    public void onAdded() {
    }

    protected void cacheCoord() {
        //Mark that we want to cache the coord and then update the coord if needed
        cacheCoord = true;
        updateCoord();
    }

    @NotNull
    protected Level getWorldNN() {
        return Objects.requireNonNull(getLevel(), "getWorldNN called before world set");
    }

    public boolean isRemote() {
        return getWorldNN().isClientSide();
    }

    public void blockRemoved() {
    }

    public void markDirtyComparator() {
    }

    @Override
    public final void setChanged() {
        setChanged(true);
    }

    public final void markForSave() {
        setChanged(false);
    }

    protected void setChanged(boolean updateComparator) {
        if (level != null) {
            long time = level.getGameTime();
            if (lastSave != time) {
                WorldUtils.markChunkDirty(level, worldPosition);
                lastSave = time;
            }
            if (updateComparator && !isRemote()) {
                markDirtyComparator();
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        BlockEntity.ComponentHelper.COMPONENTS_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(p_337987_ -> MGsOddities.logger.warn("Failed to load components: {}", p_337987_))
                .ifPresent(this::setComponents);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        return getReducedUpdateTag(provider);
    }

    @NotNull
    public CompoundTag getReducedUpdateTag(@NotNull HolderLookup.Provider provider) {
        //Add the base update tag information
        return super.getUpdateTag(provider);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, @NotNull HolderLookup.Provider provider) {
        CompoundTag tag = pkt.getTag();
        if (!tag.isEmpty()) {
            handleUpdateTag(tag, provider);
        }
    }

    public void sendUpdatePacket() {
        sendUpdatePacket(this);
    }

    public void sendUpdatePacket(BlockEntity tracking) {
        if (isRemote()) {
            MGsOddities.logger.warn("Update packet call requested from client side", new IllegalStateException());
        } else if (isRemoved()) {
            MGsOddities.logger.warn("Update packet call requested for removed tile", new IllegalStateException());
        } else if (PacketUtils.hasPlayersTracking((ServerLevel) tracking.getLevel(), tracking.getBlockPos())) {
            PacketUtils.sendToAllTracking(new PacketUpdateTile(this), tracking);
        }
    }

    protected void updateModelData() {
        requestModelDataUpdate();
        WorldUtils.updateBlock(getLevel(), getBlockPos(), getBlockState());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        updateCoord();
    }

    @Override
    public void setLevel(@NotNull Level world) {
        super.setLevel(world);
        updateCoord();
    }

    private void updateCoord() {
        if (cacheCoord && level != null) {
            cachedCoord = GlobalPos.of(level.dimension(), worldPosition);
        }
    }

    @Override
    public GlobalPos getTileGlobalPos() {
        return cacheCoord && cachedCoord != null ? cachedCoord : ITileWrapper.super.getTileGlobalPos();
    }

    public long getWorldPositionLong() {
        return worldPositionLong;
    }

    @Override
    public Chunk3D getTileChunk() {
        if (cacheCoord && cachedCoord != null) {
            return new Chunk3D(cachedCoord);
        }
        BlockPos pos = this.getBlockPos();
        return new Chunk3D(this.getLevel().dimension(), SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }
}