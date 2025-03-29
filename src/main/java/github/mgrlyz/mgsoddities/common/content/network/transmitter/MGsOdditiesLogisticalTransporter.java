package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.api.mixin.IMixinLogisticalTransporterBase;
import github.mgrlyz.mgsoddities.common.tier.transmitter.TPTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
import mekanism.common.util.NBTUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.PrimitiveIterator;

public class MGsOdditiesLogisticalTransporter extends LogisticalTransporterBase implements IMGsOdditiesUpgradeableTransmitter<LogisticalTransporterUpgradeData> {
    private @Nullable EnumColor color;

    public MGsOdditiesLogisticalTransporter(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(tile, (TransporterTier) Attribute.getTier(blockProvider, TransporterTier.class));
    }

    public void onUpdateClient() {
        TransporterStack stack;
        for(ObjectIterator var1 = this.transit.values().iterator(); var1.hasNext(); stack.progress = Math.min(100, stack.progress + TPTier.getSpeed(this.tier))) {
            stack = (TransporterStack)var1.next();
        }

    }

    public @Nullable EnumColor getColor() {
        return this.color;
    }

    public void setColor(@Nullable EnumColor c) {
        this.color = c;
    }

    public InteractionResult onConfigure(Player player, Direction side) {
        this.setColor(TransporterUtils.increment(this.getColor()));
        PathfinderCache.onChanged(new InventoryNetwork[]{(InventoryNetwork)this.getTransmitterNetwork()});
        this.getTransmitterTile().sendUpdatePacket();
        EnumColor color = this.getColor();
        player.displayClientMessage(MekanismLang.TOGGLE_COLOR.translateColored(EnumColor.GRAY, new Object[]{color == null ? MekanismLang.NONE.translateColored(EnumColor.WHITE) : color.getColoredName()}), true);
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onRightClick(Player player, Direction side) {
        EnumColor color = this.getColor();
        player.displayClientMessage(MekanismLang.CURRENT_COLOR.translateColored(EnumColor.GRAY, new Object[]{color == null ? MekanismLang.NONE.translateColored(EnumColor.WHITE) : color.getColoredName()}), true);
        return super.onRightClick(player, side);
    }

    public @Nullable LogisticalTransporterUpgradeData getUpgradeData() {
        return new LogisticalTransporterUpgradeData(this.redstoneReactive, this.getConnectionTypesRaw(), this.getColor(), this.transit, this.needsSync, this.nextId, this.delay, this.delayCount);
    }

    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof LogisticalTransporterUpgradeData;
    }

    public void parseUpgradeData(@NotNull LogisticalTransporterUpgradeData data) {
        this.redstoneReactive = data.redstoneReactive;
        this.setConnectionTypesRaw(data.connectionTypes);
        this.setColor(data.color);
        this.transit.putAll(data.transit);
        this.needsSync.putAll(data.needsSync);
        this.nextId = data.nextId;
        this.delay = data.delay;
        this.delayCount = data.delayCount;
    }

    protected void readFromNBT(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.readFromNBT(provider, nbtTags);
        this.setColor((EnumColor) NBTUtils.getEnum(nbtTags, "color", TransporterUtils::readColor));
    }

    public void writeToNBT(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.writeToNBT(provider, nbtTags);
        if (this.getColor() != null) {
            NBTUtils.writeEnum(nbtTags, "color", this.getColor());
        }

    }

    public @NotNull CompoundTag getReducedUpdateTag(@NotNull HolderLookup.@NotNull Provider provider, CompoundTag updateTag) {
        updateTag = super.getReducedUpdateTag(provider, updateTag);
        if (this.getColor() != null) {
            NBTUtils.writeEnum(updateTag, "color", this.getColor());
        }

        return updateTag;
    }

    public boolean handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.@NotNull Provider provider) {
        boolean refreshModelData = super.handleUpdateTag(tag, provider);
        EnumColor color = (EnumColor)NBTUtils.getEnum(tag, "color", EnumColor.BY_ID);
        if (this.color != color) {
            this.setColor(color);
            refreshModelData = true;
        }

        return refreshModelData;
    }

    public ITier getTier() {
        return this.tier;
    }

    public void onUpdateServer() {
        if (this.getTransmitterNetwork() != null) {
            if (this.delay > 0) {
                --this.delay;
            } else {
                this.delay = 3;
                BlockPos.MutableBlockPos inventoryPos = new BlockPos.MutableBlockPos();
                BlockPos pos = this.getBlockPos();

                for(Direction side : EnumUtils.DIRECTIONS) {
                    if (this.isConnectionType(side, ConnectionType.PULL)) {
                        inventoryPos.setWithOffset(pos, side);
                        IItemHandler inventory = (IItemHandler) Capabilities.ITEM.getCapabilityIfLoaded(this.getLevel(), inventoryPos, side.getOpposite());
                        if (inventory != null) {
                            TransitRequest request = TransitRequest.anyItem(inventory, TPTier.getPullAmount(this.tier));
                            if (!request.isEmpty()) {
                                TransitRequest.TransitResponse response = this.insert((BlockEntity)null, inventoryPos, request, this.getColor(), true, 0);
                                if (response.isEmpty()) {
                                    ++this.delayCount;
                                    this.delay = Math.min(40, (int)Math.exp((double)this.delayCount));
                                } else {
                                    response.useAll();
                                    this.delay = 10;
                                }
                            }
                        }
                    }
                }
            }

            if (!this.transit.isEmpty()) {
                long pos = this.getWorldPositionLong();
                InventoryNetwork network = (InventoryNetwork)this.getTransmitterNetwork();
                IntSet deletes = new IntOpenHashSet();
                ObjectIterator var23 = this.transit.int2ObjectEntrySet().iterator();

                while(var23.hasNext()) {
                    Int2ObjectMap.Entry<TransporterStack> entry = (Int2ObjectMap.Entry)var23.next();
                    int stackId = entry.getIntKey();
                    TransporterStack stack = (TransporterStack)entry.getValue();
                    if (stack.initiatedPath || !stack.itemStack.isEmpty() && this.recalculate(stackId, stack, Long.MAX_VALUE)) {
                        int prevProgress = stack.progress;
                        stack.progress += TPTier.getSpeed(this.tier);
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
                                        LogisticalTransporterBase transmitter = (LogisticalTransporterBase)network.getTransmitter(next);
                                        if (stack.canInsertToTransporter(transmitter, stack.getSide(this), this)) {
                                            if (transmitter instanceof IMixinLogisticalTransporterBase) {
                                                IMixinLogisticalTransporterBase mixTransmitter = (IMixinLogisticalTransporterBase)transmitter;
                                                mixTransmitter.mekanismMGsOddities$getEntity(stack, stack.progress % 100);
                                            }

                                            deletes.add(stackId);
                                            continue;
                                        }

                                        prevSet = next;
                                    } else if (stack.getPathType().hasTarget()) {
                                        Direction side = stack.getSide(this).getOpposite();
                                        IItemHandler acceptor = (IItemHandler)network.getCachedAcceptor(next, side);
                                        if (acceptor == null && stack.getPathType().isHome()) {
                                            acceptor = (IItemHandler)Capabilities.ITEM.getCapabilityIfLoaded(this.getLevel(), nextPos, side);
                                        }

                                        TransitRequest.TransitResponse response = TransitRequest.simple(stack.itemStack).addToInventory(this.getLevel(), nextPos, acceptor, 0, stack.getPathType().isHome());
                                        if (!response.isEmpty()) {
                                            ItemStack rejected = response.getRejected();
                                            if (rejected.isEmpty()) {
                                                TransporterManager.remove(this.getLevel(), stack);
                                                deletes.add(stackId);
                                                continue;
                                            }

                                            stack.itemStack = rejected;
                                        }

                                        prevSet = next;
                                    }
                                }
                            }

                            if (!this.recalculate(stackId, stack, prevSet)) {
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
                                    ConnectionType connectionType = this.getConnectionType(side);
                                    tryRecalculate = !connectionType.canSendTo() || !TransporterUtils.canInsert(this.getLevel(), BlockPos.of(stack.getDest()), stack.color, stack.itemStack, side, pathType.isHome());
                                } else {
                                    tryRecalculate = true;
                                }
                            } else {
                                long nextPos = stack.getNext(this);
                                if (nextPos == Long.MAX_VALUE) {
                                    tryRecalculate = true;
                                } else {
                                    Direction nextSide = stack.getSide(this.getWorldPositionLong(), nextPos);
                                    LogisticalTransporterBase nextTransmitter = (LogisticalTransporterBase)network.getTransmitter(nextPos);
                                    if (nextTransmitter == null && stack.getPathType().noTarget() && stack.getPath().size() == 2) {
                                        tryRecalculate = !this.getConnectionType(nextSide).canSendTo();
                                    } else {
                                        tryRecalculate = !stack.canInsertToTransporter(nextTransmitter, nextSide, this);
                                    }
                                }
                            }

                            if (tryRecalculate && !this.recalculate(stackId, stack, Long.MAX_VALUE)) {
                                deletes.add(stackId);
                            }
                        }
                    } else {
                        deletes.add(stackId);
                    }
                }

                if (!deletes.isEmpty() || !this.needsSync.isEmpty()) {
                    PacketUtils.sendToAllTracking(PacketTransporterBatch.create(pos, deletes, new Int2ObjectOpenHashMap(this.needsSync)), this.getTransmitterTile());
                    PrimitiveIterator.OfInt ofInt = deletes.iterator();

                    while(ofInt.hasNext()) {
                        this.deleteStack(ofInt.nextInt());
                    }

                    this.needsSync.clear();
                    this.getTransmitterTile().markForSave();
                }
            }
        }

    }

    private boolean recalculate(int stackId, TransporterStack stack, long from) {
        boolean noPath = stack.getPathType().noTarget() || stack.recalculatePath(TransitRequest.simple(stack.itemStack), this, 0).isEmpty();
        if (noPath && !stack.calculateIdle(this)) {
            TransporterUtils.drop(this, stack);
            return false;
        } else {
            this.needsSync.put(stackId, stack);
            if (from != Long.MAX_VALUE) {
                stack.originalLocation = from;
            }

            return true;
        }
    }
}