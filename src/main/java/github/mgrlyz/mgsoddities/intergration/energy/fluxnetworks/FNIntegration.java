package github.mgrlyz.mgsoddities.intergration.energy.fluxnetworks;

import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.util.UnitDisplayUtils;

public class FNIntegration implements IFNEnergyStorage {

    private final IStrictEnergyHandler handler;

    public FNIntegration(IStrictEnergyHandler handler) {
        this.handler = handler;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        Action action = Action.get(!simulate);
        long toInsert = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(maxReceive);
        if (toInsert == 0) {
            return 0;
        }
        if (!UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.isOneToOne()) {
            long simulatedRemainder = handler.insertEnergy(toInsert, Action.SIMULATE);
            if (simulatedRemainder == toInsert) {
                return 0;
            }
            long simulatedInserted = toInsert - simulatedRemainder;
            toInsert = convertToAndBack(simulatedInserted);
            if (toInsert == 0L) {
                return 0;
            }
        }
        long remainder = handler.insertEnergy(toInsert, action);
        if (remainder == toInsert) {
            return 0;
        }
        long inserted = toInsert - remainder;
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(inserted);
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }
        Action action = Action.get(!simulate);
        long toExtract = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(maxExtract);
        if (toExtract == 0) {
            return 0;
        }
        if (!UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.isOneToOne()) {
            long simulatedExtracted = handler.extractEnergy(toExtract, Action.SIMULATE);
            toExtract = convertToAndBack(simulatedExtracted);
            if (toExtract == 0L) {
                return 0;
            }
        }
        long extracted = handler.extractEnergy(toExtract, action);
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(extracted);
    }

    private long convertToAndBack(long joules) {
        long fe = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(joules);
        long result = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(fe);
        if (UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.getConversion() >= 1 && result % UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.getConversion() > 0) {
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(fe - 1);
        }
        return result;
    }

    @Override
    public long getEnergyStoredL() {
        long energy = 0;
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            long total = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(handler.getEnergy(container));
            if (total > Long.MAX_VALUE - energy) {
                return Long.MAX_VALUE;
            }
            energy += total;
        }
        return energy;
    }

    @Override
    public long getMaxEnergyStoredL() {
        long maxEnergy = 0;
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            long max = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(handler.getMaxEnergy(container));
            if (max > Long.MAX_VALUE - maxEnergy) {
                return Long.MAX_VALUE;
            }
            maxEnergy += max;
        }
        return maxEnergy;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}