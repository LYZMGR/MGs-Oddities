package github.mgrlyz.mgsoddities.client.render;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

@EventBusSubscriber(modid = MGsOddities.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MGsOdditiesRenderer {
    @SubscribeEvent
    public static void onStitch(TextureAtlasStitchedEvent event) {
    }

    public static int getColorARGB(float red, float green, float blue, float alpha) {
        return getColorARGB((int)(255.0F * red), (int)(255.0F * green), (int)(255.0F * blue), alpha);
    }

    public static int getColorARGB(int red, int green, int blue, float alpha) {
        if (alpha < 0.0F) {
            alpha = 0.0F;
        } else if (alpha > 1.0F) {
            alpha = 1.0F;
        }

        return FastColor.ARGB32.color((int)(255.0F * alpha), red, green, blue);
    }
}