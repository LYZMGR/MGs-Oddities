package github.mgrlyz.mgsoddities.block.attribute;

import github.mgrlyz.mgsoddities.tile.base.TileEntityMGsOddities;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StateArgumentPredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

public class Attributes {

    public static final Attribute ACTIVE = new AttributeStateActive(0);
    public static final Attribute ACTIVE_LIGHT = new AttributeStateActive(8);
    public static final Attribute ACTIVE_FULL_LIGHT = new AttributeStateActive(15);
    public static final Attribute COMPARATOR = new AttributeComparator();
    public static final Attribute INVENTORY = new AttributeInventory<>();
    public static final Attribute REDSTONE = new AttributeRedstone();
    public static final Attribute SECURITY = new AttributeSecurity();

    private Attributes() {
    }

    public static class AttributeSecurity implements Attribute {

        private AttributeSecurity() {
        }
    }
    public static class AttributeInventory<DelayedLootItemBuilder extends ConditionUserBuilder<DelayedLootItemBuilder> & FunctionUserBuilder<DelayedLootItemBuilder>> implements Attribute {

        @Nullable
        private final Predicate<DelayedLootItemBuilder> customLootBuilder;
        @SuppressWarnings("JavadocReference")
        public AttributeInventory(@Nullable Predicate<DelayedLootItemBuilder> customLootBuilder) {
            this.customLootBuilder = customLootBuilder;
        }

        private AttributeInventory() {
            this(null);
        }

        public boolean applyLoot(DelayedLootItemBuilder builder) {
            return this.customLootBuilder != null && this.customLootBuilder.test(builder);
        }
    }

    public static class AttributeComparator implements Attribute {

        private AttributeComparator() {
        }
    }

    public record AttributeComputerIntegration(String name) implements Attribute {
    }

    public static class AttributeRedstone implements Attribute {

        private AttributeRedstone() {
        }
    }

    public static class AttributeMobSpawn implements Attribute {

        public static final StateArgumentPredicate<EntityType<?>> NEVER_PREDICATE = (state, reader, pos, entityType) -> false;
        public static final AttributeMobSpawn NEVER = new AttributeMobSpawn(NEVER_PREDICATE);
        public static final AttributeMobSpawn WHEN_NOT_FORMED = new AttributeMobSpawn((state, reader, pos, entityType) -> {
            if (WorldUtils.isInsideFormedMultiblock(reader, pos, null)) {
                return false;
            }
            return state.isFaceSturdy(reader, pos, Direction.UP) && state.getLightEmission(reader, pos) < 14;
        });

        private final StateArgumentPredicate<EntityType<?>> spawningPredicate;

        public AttributeMobSpawn(StateArgumentPredicate<EntityType<?>> spawningPredicate) {
            this.spawningPredicate = spawningPredicate;
        }

        @Override
        public void adjustProperties(Properties props) {
            props.isValidSpawn(spawningPredicate);
        }
    }

    @FunctionalInterface
    public interface PathCheck {

        @Nullable
        PathType getBlockPathType(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @Nullable Mob mob);
    }

    public record AttributeCustomPathType(PathCheck pathCheck) implements Attribute {

        public static final AttributeCustomPathType WHEN_NOT_FORMED = new AttributeCustomPathType((state, level, pos, mob) ->
              WorldUtils.isInsideFormedMultiblock(level, pos, mob) ? PathType.DAMAGE_OTHER : null);
    }

    public static class AttributeRedstoneEmitter<TILE extends TileEntityMGsOddities> implements Attribute.TileAttribute<TILE> {

        private final ToIntBiFunction<TILE, Direction> redstoneFunction;

        public AttributeRedstoneEmitter(ToIntBiFunction<TILE, Direction> redstoneFunction) {
            this.redstoneFunction = redstoneFunction;
        }

        public int getRedstoneLevel(TILE tile, @NotNull Direction side) {
            return redstoneFunction.applyAsInt(tile, side);
        }
    }

    public record AttributeCustomResistance(float resistance) implements Attribute {
    }

    public static class AttributeLight implements Attribute {

        private final int light;

        public AttributeLight(int light) {
            this.light = light;
        }

        @Override
        public void adjustProperties(Properties props) {
            BlockStateHelper.applyLightLevelAdjustments(props, state -> light);
        }
    }
}