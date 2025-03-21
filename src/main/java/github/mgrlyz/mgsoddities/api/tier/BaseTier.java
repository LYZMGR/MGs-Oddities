package github.mgrlyz.mgsoddities.api.tier;

import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.IntFunction;
import mekanism.api.SupportsColorMap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public enum BaseTier implements StringRepresentable, SupportsColorMap {
    PARAGON("Paragon",new int[]{255,0,0},MapColor.GOLD),
    APOTHEOSIS("Apotheosis",new int[]{102, 153, 216},MapColor.COLOR_LIGHT_BLUE);

    public static final IntFunction<BaseTier> BY_ID = ByIdMap.continuous(BaseTier::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    public static final StreamCodec<ByteBuf, BaseTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BaseTier::ordinal);

    private final String name;
    private final MapColor mapColor;
    private TextColor textColor;
    private int[] rgbCode;
    private int argb;

    BaseTier(String name, int[] rgbCode, MapColor mapColor) {
        this.name = name;
        this.mapColor = mapColor;
        setColorFromAtlas(rgbCode);
    }

    public String getSimpleName() {
        return name;
    }

    public String getLowerName() {
        return getSimpleName().toLowerCase(Locale.ROOT);
    }

    public MapColor getMapColor() {
        return mapColor;
    }

    @Override
    public int getPackedColor() {
        return argb;
    }

    @Override
    public int[] getRgbCode() {
        return rgbCode;
    }

    @Override
    public void setColorFromAtlas(int[] color) {
        this.rgbCode = color;
        this.argb = FastColor.ARGB32.color(rgbCode[0], rgbCode[1], rgbCode[2]);
        this.textColor = TextColor.fromRgb(argb);
    }

    public TextColor getColor() {
        return this.textColor;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}