package github.mgrlyz.mgsoddities.client.events;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.client.render.MGsOdditiesRenderer;
import github.mgrlyz.mgsoddities.client.render.transmitter.*;
import github.mgrlyz.mgsoddities.common.block.attribute.MGsOdditiesAttribute;
import github.mgrlyz.mgsoddities.common.registries.MGsOdditiesTileEntityTypes;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.common.tier.ECtier;
import github.mgrlyz.mgsoddities.common.tier.FTTier;
import github.mgrlyz.mgsoddities.common.tier.TierColor;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityLogisticalTransporter;
import mekanism.api.text.EnumColor;
import mekanism.client.ClientRegistrationUtil;
import mekanism.client.render.item.TransmitterTypeDecorator;
import mekanism.common.util.WorldUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = MGsOddities.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientTick());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
               //Transmitters
        ClientRegistrationUtil.bindTileEntityRenderer(event, MGsOdditiesRenderLogisticalTransporter::new, MGsOdditiesTileEntityTypes.PARAGON_LOGISTICAL_TRANSPORTER,
                MGsOdditiesTileEntityTypes.APOTHEOSIS_LOGISTICAL_TRANSPORTER);
        ClientRegistrationUtil.bindTileEntityRenderer(event, MGsOdditiesRenderMechanicalPipe::new, MGsOdditiesTileEntityTypes.PARAGON_MECHANICAL_PIPE,
                MGsOdditiesTileEntityTypes.APOTHEOSIS_MECHANICAL_PIPE);
        ClientRegistrationUtil.bindTileEntityRenderer(event, MGsOdditiesRenderPressurizedTube::new, MGsOdditiesTileEntityTypes.PARAGON_PRESSURIZED_TUBE,
                MGsOdditiesTileEntityTypes.APOTHEOSIS_PRESSURIZED_TUBE);
        ClientRegistrationUtil.bindTileEntityRenderer(event, MGsOdditiesRenderUniversalCable::new, MGsOdditiesTileEntityTypes.PARAGON_UNIVERSAL_CABLE,
                MGsOdditiesTileEntityTypes.APOTHEOSIS_UNIVERSAL_CABLE);
        ClientRegistrationUtil.bindTileEntityRenderer(event, MGsOdditiesRenderThermodynamicConductor::new, MGsOdditiesTileEntityTypes.PARAGON_THERMODYNAMIC_CONDUCTOR,
                MGsOdditiesTileEntityTypes.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        ClientRegistrationUtil.registerBlockColorHandler(event, (state, world, pos, tintIndex) -> {
            if (tintIndex == 1) {
                FTTier tier = MGsOdditiesAttribute.getAdvanceTier(state.getBlock(), FTTier.class);
                if (tier != null) {
                    float[] color = TierColor.getColor(tier);
                    return MGsOdditiesRenderer.getColorARGB(color[0], color[1], color[2], 1);
                }
            }
            return -1;
        });

        ClientRegistrationUtil.registerBlockColorHandler(event, (state, world, pos, index) -> {
                    if (index == 1) {
                        ECtier tier = MGsOdditiesAttribute.getAdvanceTier(state.getBlock(), ECtier.class);
                        if (tier != null) {
                            float[] color = TierColor.getColor(tier);
                            return MGsOdditiesRenderer.getColorARGB(color[0], color[1], color[2], 1);
                        }
                    }
                    return -1;
                });

        ClientRegistrationUtil.registerBlockColorHandler(event, (state, world, pos, tintIndex) -> {
                    if (tintIndex == 1 && pos != null) {
                        MGsOdditiesTileEntityLogisticalTransporter transporter = WorldUtils.getTileEntity(MGsOdditiesTileEntityLogisticalTransporter.class, world, pos);
                        if (transporter != null) {
                            EnumColor renderColor = transporter.getTransmitter().getColor();
                            if (renderColor != null) {
                                return renderColor.getPackedColor();
                            }
                        }
                    }
                    return -1;
                }, MGsOdditiesBlocks.PARAGON_LOGISTICAL_TRANSPORTER, MGsOdditiesBlocks.APOTHEOSIS_LOGISTICAL_TRANSPORTER);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        TransmitterTypeDecorator.registerDecorators(event, MGsOdditiesBlocks.PARAGON_PRESSURIZED_TUBE, MGsOdditiesBlocks.APOTHEOSIS_PRESSURIZED_TUBE, MGsOdditiesBlocks.PARAGON_THERMODYNAMIC_CONDUCTOR, MGsOdditiesBlocks.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR, MGsOdditiesBlocks.PARAGON_UNIVERSAL_CABLE,MGsOdditiesBlocks.APOTHEOSIS_UNIVERSAL_CABLE);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
    }
}