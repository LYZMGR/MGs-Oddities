package github.mgrlyz.mgsoddities.content.blocktype;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.registration.impl.BlockRegistryObject;
import github.mgrlyz.mgsoddities.registries.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.registries.MGsOdditiesBlocksTypes;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.ILangEntry;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.registries.MekanismBlocks;

import java.util.Locale;
import java.util.function.Supplier;

@NothingNullByDefault
public enum FactoryType implements IHasTranslationKey.IHasEnumNameTranslationKey {
    SMELTING("smelting", MGsOdditiesLang.SMELTING, () -> MGsOdditiesBlocksTypes.ENERGIZED_SMELTER, () -> MGsOdditiesBlocks.ENERGIZED_SMELTER),
    ENRICHING("enriching", MGsOdditiesLang.ENRICHING, () -> MGsOdditiesTypes.ENRICHMENT_CHAMBER, () -> MGsOdditiesBlocks.ENRICHMENT_CHAMBER),
    CRUSHING("crushing", MGsOdditiesLang.CRUSHING, () -> MekanismBlockTypes.CRUSHER, () -> MGsOdditiesBlocks.CRUSHER),
    COMPRESSING("compressing", MGsOdditiesLang.COMPRESSING, () -> MGsOdditiesTypes.OSMIUM_COMPRESSOR, () -> MGsOdditiesBlocks.OSMIUM_COMPRESSOR),
    COMBINING("combining", MGsOdditiesLang.COMBINING, () -> MGsOdditiesTypes.COMBINER, () -> MGsOdditiesBlocks.COMBINER),
    PURIFYING("purifying", MGsOdditiesLang.PURIFYING, () -> MGsOdditiesTypes.PURIFICATION_CHAMBER, () -> MGsOdditiesBlocks.PURIFICATION_CHAMBER),
    INJECTING("injecting", MGsOdditiesLang.INJECTING, () -> MGsOdditiesTypes.CHEMICAL_INJECTION_CHAMBER, () -> MGsOdditiesBlocks.CHEMICAL_INJECTION_CHAMBER),
    INFUSING("infusing", MGsOdditiesLang.INFUSING, () -> MGsOdditiesTypes.METALLURGIC_INFUSER, () -> MGsOdditiesBlocks.METALLURGIC_INFUSER),
    SAWING("sawing", MGsOdditiesLang.SAWING, () -> MGsOdditiesTypes.PRECISION_SAWMILL, () -> MekanismBlocks.PRECISION_SAWMILL);

    private final String registryNameComponent;
    private final ILangEntry langEntry;
    private final Supplier<Machine.FactoryMachine<?>> baseMachine;
    private final Supplier<BlockRegistryObject<?, ?>> baseBlock;

    FactoryType(String registryNameComponent, ILangEntry langEntry, Supplier<Machine.FactoryMachine<?>> baseMachine, Supplier<BlockRegistryObject<?, ?>> baseBlock) {
        this.registryNameComponent = registryNameComponent;
        this.langEntry = langEntry;
        this.baseMachine = baseMachine;
        this.baseBlock = baseBlock;
    }

    public String getRegistryNameComponent() {
        return registryNameComponent;
    }

    public String getRegistryNameComponentCapitalized() {
        String name = getRegistryNameComponent();
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    public Machine.FactoryMachine<?> getBaseMachine() {
        return baseMachine.get();
    }

    public BlockRegistryObject<?, ?> getBaseBlock() {
        return baseBlock.get();
    }

    @Override
    public String getTranslationKey() {
        return langEntry.getTranslationKey();
    }
}