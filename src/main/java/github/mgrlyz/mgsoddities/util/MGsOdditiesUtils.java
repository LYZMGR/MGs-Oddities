package github.mgrlyz.mgsoddities.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.block.attribute.Attribute;
import github.mgrlyz.mgsoddities.config.MGsOdditiesConfig;
import it.unimi.dsi.fastutil.longs.Long2DoubleArrayMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.MekanismAPI;
import mekanism.api.MekanismAPITags;
import mekanism.api.MekanismItemAbilities;
import mekanism.api.Upgrade;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.text.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.common.attachments.FrequencyAware;
import mekanism.common.block.attribute.AttributeFactoryType;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.IFrequencyItem;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.UnitDisplayUtils.EnergyUnit;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.common.util.WorldUtils;
import mekanism.common.util.text.OwnerDisplay;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MekanismUtils {

    public static final float ONE_OVER_ROOT_TWO = 1 / Mth.SQRT_OF_TWO;
    public static final int TICKS_PER_HALF_SECOND = SharedConstants.TICKS_PER_SECOND / 2;

    private static final List<UUID> warnedFails = new ArrayList<>();

    public static void logMismatchedStackSize(long actual, long expected) {
        if (expected != actual) {
            MGsOddities.logger.error("Stack size changed by a different amount ({}) than requested ({}).", actual, expected);
            if (MekanismAPI.debug) {
                MGsOddities.logger.error("Location ", new Exception());
            }
        }
    }

    public static void logExpectedZero(long actual) {
        if (actual != 0L) {
            MGsOddities.logger.error("Energy value changed by a different amount ({}) than requested (zero).", actual);
            if (MekanismAPI.debug) {
                MGsOddities.logger.error("Location ", new Exception());
            }
        }
    }

    public static Component logFormat(Object message) {
        return logFormat(EnumColor.GRAY, message);
    }

    public static Component logFormat(EnumColor messageColor, Object message) {
        return MGsOdditiesLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MGsOdditiesLang.MGSODDITIES, messageColor, message);
    }

    public static boolean isTickingNormally(@Nullable Level level) {
        return level == null || level.tickRateManager().runsNormally();
    }

    @Nullable
    public static Player tryGetClientPlayer() {
        if (FMLEnvironment.dist.isClient()) {
            return MekanismClient.tryGetClientPlayer();
        }
        return null;
    }

    @NotNull
    public static String getModId(@NotNull ItemStack stack) {
        Item item = stack.getItem();
        String modid = item.getCreatorModId(stack);
        if (modid == null) {
            MGsOddities.logger.error("Unexpected null registry name for item of class type: {}", item.getClass().getSimpleName());
            return "";
        }
        return modid;
    }

    public static boolean isRightArm(LivingEntity entity, InteractionHand hand) {
        return (entity.getMainArm() == HumanoidArm.RIGHT) == (hand == InteractionHand.MAIN_HAND);
    }

    public static ItemStack getItemInHand(LivingEntity entity, HumanoidArm side) {
        if (entity.getMainArm() == side) {
            return entity.getMainHandItem();
        }
        return entity.getOffhandItem();
    }

    public static Direction getLeft(Direction orientation) {
        return orientation.getClockWise();
    }

    public static Direction getRight(Direction orientation) {
        return orientation.getCounterClockWise();
    }

    public static Direction rotate(Direction orientation, boolean supportY) {
        if (supportY) {
            return switch (orientation) {
                case UP -> Direction.NORTH;
                case DOWN -> Direction.SOUTH;
                case NORTH -> Direction.EAST;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.UP;
                case EAST -> Direction.DOWN;
            };
        }
        return orientation.getClockWise();
    }

    public static double fractionUpgrades(IUpgradeTile tile, Upgrade type) {
        if (tile.supportsUpgrade(type)) {
            return tile.getComponent().getUpgrades(type) / (double) type.getMax();
        }
        return 0;
    }

    public static float getScale(float prevScale, IExtendedFluidTank tank) {
        return getScale(prevScale, tank.getFluidAmount(), tank.getCapacity(), tank.isEmpty());
    }

    public static float getScale(float prevScale, IChemicalTank tank) {
        return getScale(prevScale, tank.getStored(), tank.getCapacity(), tank.isEmpty());
    }

    public static float getScale(float prevScale, int stored, int capacity, boolean empty) {
        return getScale(prevScale, capacity == 0 ? 0 : stored / (float) capacity, empty, stored == capacity);
    }

    public static float getScale(float prevScale, long stored, long capacity, boolean empty) {
        return getScale(prevScale, capacity == 0 ? 0 : (float) (stored / (double) capacity), empty, stored == capacity);
    }

    public static float getScale(float prevScale, IEnergyContainer container) {
        float targetScale;
        long stored = container.getEnergy();
        long capacity = container.getMaxEnergy();
        if (capacity == 0L) {
            targetScale = 0;
        } else {
            targetScale = (float) ((double) stored / capacity);
        }
        return getScale(prevScale, targetScale, container.isEmpty(), stored == capacity);
    }

    public static float getScale(float prevScale, float targetScale, boolean empty, boolean full) {
        float difference = Math.abs(prevScale - targetScale);
        if (difference > 0.01) {
            return (9 * prevScale + targetScale) / 10;
        } else if (!empty && full && difference > 0) {
            return targetScale;
        } else if (!empty && prevScale == 0) {
            return targetScale;
        }
        if (empty && prevScale < 0.01) {
            return 0;
        }
        return prevScale;
    }

    public static boolean scaleChanged(float scale, float prevScale) {
        if (Mth.equal(scale, prevScale)) {
            return scale != prevScale && scale == 0 || scale == 1 || prevScale == 1 || prevScale == 0;
        }
        return true;
    }

    public static long getBaseUsage(IUpgradeTile tile, int def) {
        if (tile.supportsUpgrades()) {
            if (tile.supportsUpgrade(Upgrade.CHEMICAL)) {
                return Math.round(def * Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(),
                        fractionUpgrades(tile, Upgrade.SPEED) - fractionUpgrades(tile, Upgrade.CHEMICAL)));
            }
        }
        return def;
    }

    public static int getTicks(IUpgradeTile tile, int def) {
        if (tile.supportsUpgrades()) {
            return Math.max(1, MathUtils.clampToInt(def * Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(), -fractionUpgrades(tile, Upgrade.SPEED))));
        }
        return def;
    }

    public static long getEnergyPerTick(IUpgradeTile tile, long def) {
        if (tile.supportsUpgrades()) {
            return MathUtils.ceilToLong(def * Math.pow(
                    MGsOdditiesConfig.general.maxUpgradeMultiplier.get(),
                    2 * fractionUpgrades(tile, Upgrade.SPEED) - fractionUpgrades(tile, Upgrade.ENERGY)
            ));
        }
        return def;
    }

    public static double getGasPerTickMeanMultiplier(IUpgradeTile tile) {
        if (tile.supportsUpgrades()) {
            if (tile.supportsUpgrade(Upgrade.CHEMICAL)) {
                return Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(), 2 * fractionUpgrades(tile, Upgrade.SPEED) - fractionUpgrades(tile, Upgrade.CHEMICAL));
            }
            return Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(), fractionUpgrades(tile, Upgrade.SPEED));
        }
        return 1;
    }

    public static long getMaxEnergy(IUpgradeTile tile, long def) {
        if (tile.supportsUpgrades()) {
            return MathUtils.clampToLong(def * (Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(), fractionUpgrades(tile, Upgrade.ENERGY))));
        }
        return def;
    }

    public static long getMaxEnergy(int energyUpgrades, long def) {
        return MathUtils.clampToLong(def * (Math.pow(MGsOdditiesConfig.general.maxUpgradeMultiplier.get(), energyUpgrades / (double) Upgrade.ENERGY.getMax())));
    }

    public static ResourceLocation getResource(mekanism.common.util.MekanismUtils.ResourceType type, String name) {
        return MGsOddities.rl(type.getPrefix() + name);
    }

    public static boolean lighterThanAirGas(FluidStack stack) {
        return stack.is(Tags.Fluids.GASEOUS) && stack.getFluidType().getDensity(stack) <= 0;
    }

    public static boolean isLiquidBlock(Block block) {
        return block instanceof LiquidBlock || block instanceof BubbleColumnBlock;
    }

    public static BlockHitResult rayTrace(Player player) {
        return rayTrace(player, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult rayTrace(Player player, ClipContext.Fluid fluidMode) {
        return rayTrace(player, player.blockInteractionRange(), fluidMode);
    }

    public static BlockHitResult rayTrace(Player player, double reach) {
        return rayTrace(player, reach, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult rayTrace(Player player, double reach, ClipContext.Fluid fluidMode) {
        Vec3 headVec = getHeadVec(player);
        Vec3 lookVec = player.getViewVector(1);
        Vec3 endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return player.level().clip(new ClipContext(headVec, endVec, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    private static Vec3 getHeadVec(Player player) {
        double posY = player.getY() + player.getEyeHeight();
        if (player.isCrouching()) {
            posY -= 0.08;
        }
        return new Vec3(player.getX(), posY, player.getZ());
    }

    public static void addFrequencyItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IFrequencyItem frequencyItem)) {
            return;
        }
        DataComponentType<? extends FrequencyAware<?>> frequencyComponent = MekanismDataComponents.getFrequencyComponent(frequencyItem.getFrequencyType());
        if (frequencyComponent == null) {
            return;
        }
        FrequencyAware<?> frequencyAware = stack.get(frequencyComponent);
        if (frequencyAware != null) {
            FrequencyIdentity identity = frequencyAware.identity().orElse(null);
            if (identity != null) {
                tooltip.add(MGsOdditiesLang.FREQUENCY.translateColored(EnumColor.INDIGO, EnumColor.GRAY, identity.key()));
                UUID ownerUUID = frequencyAware.getOwner();
                if (ownerUUID != null) {
                    String owner = OwnerDisplay.getOwnerName(mekanism.common.util.MekanismUtils.tryGetClientPlayer(), frequencyAware.getOwner(), null);
                    if (owner != null) {
                        tooltip.add(MGsOdditiesLang.OWNER.translateColored(EnumColor.INDIGO, EnumColor.GRAY, owner));
                    }
                }
                tooltip.add(MGsOdditiesLang.MODE.translateColored(EnumColor.INDIGO, EnumColor.GRAY, identity.securityMode()));
            }
        }
    }

    public static long calculateUsage(long capacity) {
        return Math.max((long) (0.005 * capacity), 1);
    }

    public static Component getEnergyDisplayShort(long energy) {
        EnergyUnit configured = EnergyUnit.getConfigured();
        return UnitDisplayUtils.getDisplayShort(configured.convertToDouble(energy), configured);
    }

    public static long convertToJoules(long energy) {
        return EnergyUnit.getConfigured().convertFrom(energy);
    }

    public static Component getTemperatureDisplay(double temp, TemperatureUnit unit, boolean shift) {
        double tempKelvin = unit.convertToK(temp, true);
        return UnitDisplayUtils.getDisplayShort(tempKelvin, MGsOdditiesConfig.common.tempUnit.get(), shift);
    }

    public static CraftingInput.Positioned getCraftingInput(int width, int height, List<ItemStack> slots, boolean resize) {
        if (width * height != slots.size()) {
            throw new IllegalStateException("Expected there to be a slot for every index in a " + width + " by " + height + " grid.");
        }
        List<ItemStack> stacks = new ArrayList<>(slots.size());
        for (ItemStack slot : slots) {
            if (resize) {
                stacks.add(slot.copyWithCount(1));
            } else {
                stacks.add(slot.copy());
            }
        }
        return CraftingInput.ofPositioned(width, height, stacks);
    }

    public static CraftingInput.Positioned getCraftingInputSlots(int width, int height, List<IInventorySlot> slots, boolean resize) {
        if (width * height != slots.size()) {
            throw new IllegalStateException("Expected there to be a slot for every index in a " + width + " by " + height + " grid.");
        }
        List<ItemStack> stacks = new ArrayList<>(slots.size());
        for (IInventorySlot slot : slots) {
            ItemStack stack = slot.getStack();
            if (resize) {
                stacks.add(stack.copyWithCount(1));
            } else {
                stacks.add(stack.copy());
            }
        }
        return CraftingInput.ofPositioned(width, height, stacks);
    }

    public static boolean canUseAsWrench(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else if (stack.canPerformAction(MekanismItemAbilities.WRENCH_DISMANTLE)) {
            return true;
        } else if (stack.is(MekanismTags.Items.CONFIGURATORS)) {
            return !stack.canPerformAction(MekanismItemAbilities.WRENCH_ROTATE) &&
                    !stack.canPerformAction(MekanismItemAbilities.WRENCH_EMPTY) &&
                    !stack.canPerformAction(MekanismItemAbilities.WRENCH_CONFIGURE);
        }
        return false;
    }

    @NotNull
    public static String getLastKnownUsername(@Nullable UUID uuid) {
        if (uuid == null) {
            return "<???>";
        }
        String ret = UsernameCache.getLastKnownUsername(uuid);
        if (ret == null && !warnedFails.contains(uuid) && EffectiveSide.get().isServer()) {
            Optional<GameProfile> gp = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
            if (gp.isPresent()) {
                ret = gp.get().getName();
            }
        }
        if (ret == null && !warnedFails.contains(uuid)) {
            MGsOddities.logger.warn("Failed to retrieve username for UUID {}, you might want to add it to the JSON cache", uuid);
            warnedFails.add(uuid);
        }
        return ret == null ? "<" + uuid + ">" : ret;
    }

    public static void speedUpEffectSafely(LivingEntity entity, MobEffectInstance effectInstance) {
        if (effectInstance.getDuration() > 0) {
            int remainingDuration = effectInstance.tickDownDuration();
            if (remainingDuration == 0 && effectInstance.hiddenEffect != null) {
                effectInstance.setDetailsFrom(effectInstance.hiddenEffect);
                effectInstance.hiddenEffect = effectInstance.hiddenEffect.hiddenEffect;
                onChangedPotionEffect(entity, effectInstance, true);
            }
        }
    }

    public static boolean shouldSpeedUpEffect(MobEffectInstance effectInstance) {
        return effectInstance.getCures().contains(EffectCures.MILK) && !effectInstance.getEffect().getDelegate().is(MekanismAPITags.MobEffects.SPEED_UP_BLACKLIST);
    }

    private static void onChangedPotionEffect(LivingEntity entity, MobEffectInstance effectInstance, boolean reapply) {
        entity.effectsDirty = true;
        if (reapply && !entity.level().isClientSide) {
            MobEffect effect = effectInstance.getEffect().value();
            effect.removeAttributeModifiers(entity.getAttributes());
            effect.addAttributeModifiers(entity.getAttributes(), effectInstance.getAmplifier());
            entity.refreshDirtyAttributes();
        }
        if (!entity.level().isClientSide) {
            entity.sendEffectToPassengers(effectInstance);
        }
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), effectInstance, false));
            CriteriaTriggers.EFFECTS_CHANGED.trigger(player, null);
        }
    }

    public static boolean isSameTypeFactory(Block block, Block factoryBlockType) {
        AttributeFactoryType attribute = Attribute.get(block, AttributeFactoryType.class);
        if (attribute != null) {
            AttributeFactoryType otherType = Attribute.get(factoryBlockType, AttributeFactoryType.class);
            return otherType != null && attribute.getFactoryType() == otherType.getFactoryType();
        }
        return false;
    }

    @SafeVarargs
    public static InteractionResult performActions(InteractionResult firstAction, Supplier<InteractionResult>... secondaryActions) {
        if (firstAction.consumesAction()) {
            return firstAction;
        }
        InteractionResult result = firstAction;
        boolean hasFailed = result == InteractionResult.FAIL;
        for (Supplier<InteractionResult> secondaryAction : secondaryActions) {
            result = secondaryAction.get();
            if (result.consumesAction()) {
                return result;
            }
            hasFailed &= result == InteractionResult.FAIL;
        }
        if (hasFailed) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static int redstoneLevelFromContents(long amount, long capacity) {
        double fractionFull = capacity == 0 ? 0 : ((double) amount / capacity);
        return Mth.floor(fractionFull * 14.0D) + (fractionFull > 0 ? 1 : 0);
    }

    public static int redstoneLevelFromContents(List<IInventorySlot> slots) {
        long totalCount = 0;
        long totalLimit = 0;
        for (IInventorySlot slot : slots) {
            if (slot.isEmpty()) {
                totalLimit += slot.getLimit(ItemStack.EMPTY);
            } else {
                totalCount += slot.getCount();
                totalLimit += slot.getLimit(slot.getStack());
            }
        }
        return redstoneLevelFromContents(totalCount, totalLimit);
    }

    public static boolean isPlayingMode(Player player) {
        return !player.isCreative() && !player.isSpectator();
    }

    public static List<String> getParameterNames(@Nullable JsonObject classMethods, String method, String signature) {
        if (classMethods != null) {
            JsonObject signatures = classMethods.getAsJsonObject(method);
            if (signatures != null) {
                JsonElement params = signatures.get(signature);
                if (params != null) {
                    if (params.isJsonArray()) {
                        JsonArray paramArray = params.getAsJsonArray();
                        List<String> paramNames = new ArrayList<>(paramArray.size());
                        for (JsonElement param : paramArray) {
                            paramNames.add(param.getAsString());
                        }
                        return Collections.unmodifiableList(paramNames);
                    }
                    return Collections.singletonList(params.getAsString());
                }
            }
        }
        return Collections.emptyList();
    }

    @FunctionalInterface
    public interface ModifyPlayerBounding {

        AABB modify(AABB bounding, double data);
    }

    public static Map<FluidType, mekanism.common.util.MekanismUtils.FluidInDetails> getFluidsIn(Player player, double data, mekanism.common.util.MekanismUtils.ModifyPlayerBounding modifyBoundingBox) {
        AABB bb = modifyBoundingBox.modify(player.getBoundingBox().deflate(0.001), data);
        int xMin = Mth.floor(bb.minX);
        int xMax = Mth.ceil(bb.maxX);
        int yMin = Mth.floor(bb.minY);
        int yMax = Mth.ceil(bb.maxY);
        int zMin = Mth.floor(bb.minZ);
        int zMax = Mth.ceil(bb.maxZ);
        if (!player.level().hasChunksAt(xMin, yMin, zMin, xMax, yMax, zMax)) {
            return Collections.emptyMap();
        }
        Map<FluidType, mekanism.common.util.MekanismUtils.FluidInDetails> fluidsIn = new IdentityHashMap<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = xMin; x < xMax; ++x) {
            for (int y = yMin; y < yMax; ++y) {
                for (int z = zMin; z < zMax; ++z) {
                    mutablePos.set(x, y, z);
                    FluidState fluidState = player.level().getFluidState(mutablePos);
                    if (!fluidState.isEmpty()) {
                        double fluidY = y + fluidState.getHeight(player.level(), mutablePos);
                        if (bb.minY <= fluidY) {
                            mekanism.common.util.MekanismUtils.FluidInDetails details = fluidsIn.computeIfAbsent(fluidState.getFluidType(), f -> new mekanism.common.util.MekanismUtils.FluidInDetails());
                            details.positions.put(mutablePos.immutable(), fluidState);
                            double actualFluidHeight;
                            if (fluidY > bb.maxY) {
                                actualFluidHeight = bb.maxY - Math.max(bb.minY, y);
                            } else {
                                actualFluidHeight = fluidY - Math.max(bb.minY, y);
                            }
                            details.heights.merge(ChunkPos.asLong(x, z), actualFluidHeight, Double::sum);
                        }
                    }
                }
            }
        }
        return fluidsIn;
    }

    public static void veinMineArea(IEnergyContainer energyContainer, long energyRequired, long baseBlastEnergy, long baseVeinEnergy,
                                    Level world, BlockPos pos, ServerPlayer player, ItemStack stack, Item usedTool, Object2IntMap<BlockPos> found, mekanism.common.util.MekanismUtils.BlastEnergyFunction blastEnergy,
                                    mekanism.common.util.MekanismUtils.VeinEnergyFunction veinEnergy) {
        long energyUsed = 0L;
        long energyAvailable = energyContainer.getEnergy();
        energyAvailable = energyAvailable - energyRequired;
        Stat<Item> itemStat = Stats.ITEM_USED.get(usedTool);
        for (ObjectIterator<Object2IntMap.Entry<BlockPos>> iterator = Object2IntMaps.fastIterator(found); iterator.hasNext(); ) {
            Object2IntMap.Entry<BlockPos> foundEntry = iterator.next();
            BlockPos foundPos = foundEntry.getKey();
            if (pos.equals(foundPos)) {
                continue;
            }
            BlockState targetState = world.getBlockState(foundPos);
            if (targetState.isAir()) {
                continue;
            }
            float hardness = targetState.getDestroySpeed(world, foundPos);
            if (hardness == Block.INDESTRUCTIBLE) {
                continue;
            }
            int distance = foundEntry.getIntValue();
            long destroyEnergy = distance == 0 ? blastEnergy.calc(baseBlastEnergy, hardness) : veinEnergy.calc(baseVeinEnergy, hardness, distance, targetState);
            if (energyUsed + destroyEnergy >= energyAvailable) {
                continue;
            }
            BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(world, player.gameMode.getGameModeForPlayer(), player, foundPos, targetState);
            if (event.isCanceled()) {
                continue;
            }
            FluidState fluidState = targetState.getFluidState();
            BlockEntity tileEntity = WorldUtils.getTileEntity(world, foundPos);
            targetState = targetState.getBlock().playerWillDestroy(world, foundPos, targetState, player);
            Block block = targetState.getBlock();
            if (targetState.onDestroyedByPlayer(world, foundPos, player, true, fluidState)) {
                block.destroy(world, foundPos, targetState);
                block.playerDestroy(world, player, foundPos, targetState, tileEntity, stack);
                player.awardStat(itemStat);
                energyUsed += destroyEnergy;
            }
        }
        energyContainer.extract(energyUsed, Action.EXECUTE, AutomationType.MANUAL);
    }

    public enum ResourceType {
        GUI("gui"),
        GUI_BUTTON("gui/button"),
        GUI_BAR("gui/bar"),
        GUI_GAUGE("gui/gauge"),
        GUI_HUD("gui/hud"),
        GUI_ICONS("gui/icons"),
        GUI_MODE("gui/mode"),
        GUI_PROGRESS("gui/progress"),
        GUI_RADIAL("gui/radial"),
        GUI_SLOT("gui/slot"),
        GUI_TAB("gui/tabs"),
        SOUND("sound"),
        RENDER("render"),
        TEXTURE_BLOCKS("textures/block"),
        TEXTURE_ITEMS("textures/item"),
        MODEL("models"),
        INFUSE("infuse"),
        PIGMENT("pigment"),
        SLURRY("slurry");

        private final String prefix;

        ResourceType(String s) {
            prefix = s;
        }

        public String getPrefix() {
            return prefix + "/";
        }
    }

    public static class FluidInDetails {

        private final Map<BlockPos, FluidState> positions = new HashMap<>();
        private final Long2DoubleMap heights = new Long2DoubleArrayMap();

        public Map<BlockPos, FluidState> getPositions() {
            return positions;
        }

        public double getMaxHeight() {
            return heights.values().doubleStream().max().orElse(0);
        }
    }

    @FunctionalInterface
    public interface BlastEnergyFunction {

        long calc(long baseBlastEnergy, float hardness);
    }

    @FunctionalInterface
    public interface VeinEnergyFunction {

        long calc(long baseVeinEnergy, float hardness, int distance, BlockState state);
    }
}