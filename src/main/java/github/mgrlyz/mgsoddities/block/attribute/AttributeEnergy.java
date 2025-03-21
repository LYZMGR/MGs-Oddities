package github.mgrlyz.mgsoddities.block.attribute;

import mekanism.api.functions.ConstantPredicates;
import mekanism.api.math.MathUtils;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class AttributeEnergy implements Attribute {

    private LongSupplier energyUsage = ConstantPredicates.ZERO_LONG;
    private LongSupplier energyStorage = () -> MathUtils.multiplyClamped(energyUsage.getAsLong(), 20 * SharedConstants.TICKS_PER_SECOND);

    public AttributeEnergy(@Nullable LongSupplier energyUsage, @Nullable LongSupplier energyStorage) {
        if (energyUsage != null) {
            this.energyUsage = energyUsage;
        }
        if (energyStorage != null) {
            this.energyStorage = energyStorage;
        }
    }

    public long getUsage() {
        return energyUsage.getAsLong();
    }

    public long getConfigStorage() {
        return energyStorage.getAsLong();
    }

    public long getStorage() {
        return Math.max(getConfigStorage(), getUsage());
    }
}
