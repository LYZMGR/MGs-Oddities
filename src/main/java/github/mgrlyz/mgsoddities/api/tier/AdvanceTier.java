package github.mgrlyz.mgsoddities.api.tier;

import io.netty.buffer.ByteBuf;
import mekanism.api.SupportsColorMap;
import mekanism.api.math.MathUtils;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.IntFunction;

public enum AdvanceTier implements StringRepresentable, SupportsColorMap {
    PARAGON("Paragon",new int[]{250, 238, 77}, MapColor.GOLD),
    APOTHEOSIS("Apotheosis",new int[]{102, 153, 216}, MapColor.COLOR_LIGHT_BLUE);

    public static final IntFunction<AdvanceTier> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, AdvanceTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    private static final AdvanceTier[] BASE_TIERS = values();
    private final String name;
    private final MapColor mapColor;
    private TextColor textColor;
    private int[] rgbCode;
    private int argb;

    private AdvanceTier(String name, int[] rgbCode, MapColor mapColor) {
        this.name = name;
        this.mapColor = mapColor;
        this.setColorFromAtlas(rgbCode);
    }


    public String getSimpleName() {
        return this.name;
    }

    public String getLowerName() {
        return this.getSimpleName().toLowerCase(Locale.ROOT);
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    public int getPackedColor() {
        return this.argb;
    }

    public int[] getRgbCode() {
        return this.rgbCode;
    }

    public void setColorFromAtlas(int[] color) {
        this.rgbCode = color;
        this.argb = FastColor.ARGB32.color(this.rgbCode[0], this.rgbCode[1], this.rgbCode[2]);
        this.textColor = TextColor.fromRgb(this.argb);
    }

    public TextColor getColor() {
        return this.textColor;
    }

    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static AdvanceTier byIndexStatic(int index) {
        return (AdvanceTier) MathUtils.getByIndexMod(BASE_TIERS, index);
    }
}