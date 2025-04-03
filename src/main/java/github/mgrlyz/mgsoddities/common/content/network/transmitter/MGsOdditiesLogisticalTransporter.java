package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.api.mixin.IMixinLogisticalTransporterBase;
import github.mgrlyz.mgsoddities.common.tier.transmitter.TPTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mekanism.api.SerializationConstants;
import mekanism.api.text.EnumColor;
import mekanism.api.tier.ITier;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.network.InventoryNetwork;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.PathfinderCache;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.network.PacketUtils;
import mekanism.common.network.to_client.transmitter.PacketTransporterBatch;
import mekanism.common.tier.TransporterTier;
import mekanism.common.upgrade.transmitter.LogisticalTransporterUpgradeData;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.PrimitiveIterator;

public class MGsOdditiesLogisticalTransporter extends LogisticalTransporterBase implements IMGsOdditiesUpgradeableTransmitter<LogisticalTransporterUpgradeData> {
    @Nullable
    private EnumColor color;
    public MGsOdditiesLogisticalTransporter(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(tile, Attribute.getTier(blockProvider, TransporterTier.class));
    }

    @Override
    public void onUpdateClient() {
        for (TransporterStack stack : transit.values()) {
            stack.progress = Math.min(100, stack.progress + TPTier.getSpeed(tier));
        }
    }

    @Nullable
    @Override
    public EnumColor getColor() {
        return color;
    }

    public void setColor(@Nullable EnumColor c) {
        color = c;
    }

    @Override
    public InteractionResult onConfigure(Player player, Direction side) {
        setColor(TransporterUtils.increment(getColor()));
        PathfinderCache.onChanged(getTransmitterNetwork());
        getTransmitterTile().sendUpdatePacket();
        EnumColor color = getColor();
        player.displayClientMessage(MekanismLang.TOGGLE_COLOR.translateColored(EnumColor.GRAY, color == null ? MekanismLang.NONE.translateColored(EnumColor.WHITE) : color.getColoredName()), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onRightClick(Player player, Direction side) {
        EnumColor color = getColor();
        player.displayClientMessage(MekanismLang.CURRENT_COLOR.translateColored(EnumColor.GRAY, color == null ? MekanismLang.NONE.translateColored(EnumColor.WHITE) : color.getColoredName()), true);
        return super.onRightClick(player, side);
    }

    @Nullable
    @Override
    public LogisticalTransporterUpgradeData getUpgradeData() {
        return new LogisticalTransporterUpgradeData(redstoneReactive, getConnectionTypesRaw(), getColor(), transit, needsSync, nextId, delay, delayCount);
    }

    @Override
    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof LogisticalTransporterUpgradeData;
    }

    @Override
    public void parseUpgradeData(@NotNull LogisticalTransporterUpgradeData data) {
        redstoneReactive = data.redstoneReactive;
        setConnectionTypesRaw(data.connectionTypes);
        setColor(data.color);
        transit.putAll(data.transit);
        needsSync.putAll(data.needsSync);
        nextId = data.nextId;
        delay = data.delay;
        delayCount = data.delayCount;
    }

    @Override
    protected void readFromNBT(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.readFromNBT(provider, nbtTags);
        setColor(NBTUtils.getEnum(nbtTags, SerializationConstants.COLOR, TransporterUtils::readColor));
    }

    @Override
    public void writeToNBT(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.writeToNBT(provider, nbtTags);
        if (getColor() != null) {
            NBTUtils.writeEnum(nbtTags, SerializationConstants.COLOR, getColor());
        }
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag(@NotNull HolderLookup.Provider provider, CompoundTag updateTag) {
        updateTag = super.getReducedUpdateTag(provider, updateTag);
        if (getColor() != null) {
            NBTUtils.writeEnum(updateTag, SerializationConstants.COLOR, getColor());
        }
        return updateTag;
    }

    @Override
    public boolean handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        boolean refreshModelData = super.handleUpdateTag(tag, provider);
        EnumColor color = NBTUtils.getEnum(tag, SerializationConstants.COLOR, EnumColor.BY_ID);
        if (this.color != color) {
            setColor(color);
            refreshModelData = true;
        }
        return refreshModelData;
    }

    @Override
    public ITier getTier() {
        return tier;
    }

    @Override
    public void onUpdateServer() {
        if (getTransmitterNetwork() != null) {
            if (delay > 0) {
                delay--;
            } else {
                delay = 3;
                BlockPos.MutableBlockPos inventoryPos = new BlockPos.MutableBlockPos();
                BlockPos pos = getBlockPos();
                for (Direction side : EnumUtils.DIRECTIONS) {
                    if (!isConnectionType(side, ConnectionType.PULL)) {
                        continue;
                    }
                    inventoryPos.setWithOffset(pos, side);
                    IItemHandler inventory = Capabilities.ITEM.getCapabilityIfLoaded(getLevel(), inventoryPos, side.getOpposite());
                    if (inventory != null) {
                        TransitRequest request = TransitRequest.anyItem(inventory, TPTier.getPullAmount(tier));
                        if (!request.isEmpty()) {
                            TransitRequest.TransitResponse response = insert(null, inventoryPos, request, getColor(), true, 0);
                            if (response.isEmpty()) {
                                delayCount++;
                                delay = Math.min(2 * SharedConstants.TICKS_PER_SECOND, (int) Math.exp(delayCount));
                            } else {
                                response.useAll();
                                delay = MekanismUtils.TICKS_PER_HALF_SECOND;
                            }
                        }
                    }
                }
            }
            if (!transit.isEmpty()) {
                long pos = getWorldPositionLong();
                InventoryNetwork network = getTransmitterNetwork();
                IntSet deletes = new IntOpenHashSet();
                for (Int2ObjectMap.Entry<TransporterStack> entry : transit.int2ObjectEntrySet()) {
                    int stackId = entry.getIntKey();
                    TransporterStack stack = entry.getValue();
                    if (!stack.initiatedPath) {
                        if (stack.itemStack.isEmpty() || !recalculate(stackId, stack, Long.MAX_VALUE)) {
                            deletes.add(stackId);
                            continue;
                        }
                    }

                    int prevProgress = stack.progress;
                    stack.progress += TPTier.getSpeed(tier);
                    if (stack.progress >= 100) {
                        long prevSet = Long.MAX_VALUE;
                        if (stack.hasPath()) {
                            int currentIndex = stack.getPath().indexOf(pos);
                            if (currentIndex == 0) {
                                deletes.add(stackId);
                                continue;
                            }
                            long next = stack.getPath().getLong(currentIndex - 1);
                            if (next != Long.MAX_VALUE) {
                                BlockPos nextPos = BlockPos.of(next);
                                if (!stack.isFinal(this)) {
                                    LogisticalTransporterBase transmitter = network.getTransmitter(next);
                                    if (stack.canInsertToTransporter(transmitter, stack.getSide(this), this)) {
                                        if (transmitter instanceof IMixinLogisticalTransporterBase mixTransmitter) {
                                            mixTransmitter.mekanismMGsOddities$getEntity(stack, stack.progress % 100);
                                        }
                                        deletes.add(stackId);
                                        continue;
                                    }
                                    prevSet = next;
                                } else if (stack.getPathType().hasTarget()) {
                                    Direction side = stack.getSide(this).getOpposite();
                                    IItemHandler acceptor = network.getCachedAcceptor(next, side);
                                    if (acceptor == null && stack.getPathType().isHome()) {
                                        acceptor = Capabilities.ITEM.getCapabilityIfLoaded(getLevel(), nextPos, side);
                                    }
                                    TransitRequest.TransitResponse response = TransitRequest.simple(stack.itemStack).addToInventory(getLevel(), nextPos, acceptor, 0,
                                            stack.getPathType().isHome());
                                    if (!response.isEmpty()) {
                                        ItemStack rejected = response.getRejected();
                                        if (rejected.isEmpty()) {
                                            TransporterManager.remove(getLevel(), stack);
                                            deletes.add(stackId);
                                            continue;
                                        }
                                        stack.itemStack = rejected;
                                    }
                                    prevSet = next;
                                }
                            }
                        }
                        if (!recalculate(stackId, stack, prevSet)) {
                            deletes.add(stackId);
                        } else if (prevSet == Long.MAX_VALUE) {
                            stack.progress = 50;
                        } else {
                            stack.progress = 0;
                        }
                    } else if (prevProgress < 50 && stack.progress >= 50) {
                        boolean tryRecalculate;
                        if (stack.isFinal(this)) {
                            TransporterStack.Path pathType = stack.getPathType();
                            if (pathType.hasTarget()) {
                                Direction side = stack.getSide(this);
                                ConnectionType connectionType = getConnectionType(side);
                                tryRecalculate = !connectionType.canSendTo() ||
                                        !TransporterUtils.canInsert(getLevel(), BlockPos.of(stack.getDest()), stack.color, stack.itemStack, side, pathType.isHome());
                            } else {
                                tryRecalculate = true;
                            }
                        } else {
                            long nextPos = stack.getNext(this);
                            if (nextPos == Long.MAX_VALUE) {
                                tryRecalculate = true;
                            } else {
                                Direction nextSide = stack.getSide(getWorldPositionLong(), nextPos);
                                LogisticalTransporterBase nextTransmitter = network.getTransmitter(nextPos);
                                if (nextTransmitter == null && stack.getPathType().noTarget() && stack.getPath().size() == 2) {
                                    tryRecalculate = !getConnectionType(nextSide).canSendTo();
                                } else {
                                    tryRecalculate = !stack.canInsertToTransporter(nextTransmitter, nextSide, this);
                                }
                            }
                        }
                        if (tryRecalculate && !recalculate(stackId, stack, Long.MAX_VALUE)) {
                            deletes.add(stackId);
                        }
                    }
                }

                if (!deletes.isEmpty() || !needsSync.isEmpty()) {
                    PacketUtils.sendToAllTracking(PacketTransporterBatch.create(pos, deletes, new Int2ObjectOpenHashMap<>(needsSync)), getTransmitterTile());
                    PrimitiveIterator.OfInt ofInt = deletes.iterator();
                    while (ofInt.hasNext()) {
                        deleteStack(ofInt.nextInt());
                    }

                    needsSync.clear();

                    getTransmitterTile().markForSave();
                }
            }
        }
    }

    private boolean recalculate(int stackId, TransporterStack stack, long from) {
        boolean noPath = stack.getPathType().noTarget() || stack.recalculatePath(TransitRequest.simple(stack.itemStack), this, 0).isEmpty();
        if (noPath && !stack.calculateIdle(this)) {
            TransporterUtils.drop(this, stack);
            return false;
        }

        needsSync.put(stackId, stack);
        if (from != Long.MAX_VALUE) {
            stack.originalLocation = from;
        }
        return true;
    }
}