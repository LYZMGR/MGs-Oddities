package github.mgrlyz.mgsoddities;

import com.mojang.logging.LogUtils;
import github.mgrlyz.mgsoddities.common.capabilities.MGsOdditiesCapabilities;
import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import github.mgrlyz.mgsoddities.common.recipe.MGsOdditiesRecipe;
import github.mgrlyz.mgsoddities.common.recipe.MGsOdditiesRecipeSerializer;
import github.mgrlyz.mgsoddities.common.registries.MGsOdditiesCreativeTabs;
import github.mgrlyz.mgsoddities.common.registries.MGsOdditiesTileEntityTypes;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.common.registries.item.MGsOdditiesItems;
import mekanism.common.command.CommandMek;
import mekanism.common.lib.Version;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(MGsOddities.MODID)
public class MGsOddities {
    public static final String MODID = "mgsoddities";
    public static final String MOD_NAME = "MG's Oddities";
    public static MGsOddities instance;
    public final Version versionNumber;
    private static final Logger LOGGER = LogUtils.getLogger();
    public MGsOddities(ModContainer modContainer, IEventBus modEventBus) {
        instance = this;
        MGsOdditiesConfig.registerConfigs(modContainer);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        modEventBus.addListener(MGsOdditiesCapabilities::registerCapabilities);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(MGsOdditiesConfig::onConfigLoad);
        MGsOdditiesItems.register(modEventBus);
        MGsOdditiesBlocks.register(modEventBus);
        MGsOdditiesTileEntityTypes.register(modEventBus);
        MGsOdditiesCreativeTabs.register(modEventBus);
        versionNumber = new Version(modContainer);

        MGsOdditiesRecipe.RECIPE_TYPES.register(modEventBus);
        MGsOdditiesRecipeSerializer.RECIPE_SERIALIZER.register(modEventBus);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(CommandMek.register());
    }
}