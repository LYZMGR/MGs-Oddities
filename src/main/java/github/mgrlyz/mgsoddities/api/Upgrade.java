package github.mgrlyz.mgsoddities.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.APILang;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.ILangEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

@NothingNullByDefault
public enum Upgrade implements IHasTranslationKey.IHasEnumNameTranslationKey, StringRepresentable {
    SPEED("speed", APILang.UPGRADE_SPEED, APILang.UPGRADE_SPEED_DESCRIPTION, 8, EnumColor.RED),
    ENERGY("energy", APILang.UPGRADE_ENERGY, APILang.UPGRADE_ENERGY_DESCRIPTION, 8, EnumColor.BRIGHT_GREEN),
    FILTER("filter", APILang.UPGRADE_FILTER, APILang.UPGRADE_FILTER_DESCRIPTION, 1, EnumColor.DARK_AQUA),
    CHEMICAL("chemical", APILang.UPGRADE_CHEMICAL, APILang.UPGRADE_CHEMICAL_DESCRIPTION, 8, EnumColor.YELLOW),
    MUFFLING("muffling", APILang.UPGRADE_MUFFLING, APILang.UPGRADE_MUFFLING_DESCRIPTION, 1, EnumColor.INDIGO),
    ANCHOR("anchor", APILang.UPGRADE_ANCHOR, APILang.UPGRADE_ANCHOR_DESCRIPTION, 1, EnumColor.DARK_GREEN),
    STONE_GENERATOR("stone_generator", APILang.UPGRADE_STONE_GENERATOR, APILang.UPGRADE_STONE_GENERATOR_DESCRIPTION, 1, EnumColor.ORANGE);

    public static final Codec<Upgrade> CODEC;

    static {
        Upgrade[] values = values();
        Function<String, Upgrade> nameLookup = StringRepresentable.createNameLookup(values, Function.identity());
        Function<String, Upgrade> remapper = it -> "gas".equals(it) ? CHEMICAL : nameLookup.apply(it);
        CODEC = new EnumCodec<>(values, remapper);
    }

    public static final IntFunction<Upgrade> BY_ID = ByIdMap.continuous(Upgrade::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, Upgrade> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Upgrade::ordinal);

    private final String name;
    private final ILangEntry langKey;
    private final ILangEntry descLangKey;
    private final int maxStack;
    private final EnumColor color;

    Upgrade(String name, ILangEntry langKey, ILangEntry descLangKey, int maxStack, EnumColor color) {
        this.name = name;
        this.langKey = langKey;
        this.descLangKey = descLangKey;
        this.maxStack = maxStack;
        this.color = color;
    }

    public static Map<Upgrade, Integer> buildMap(@Nullable CompoundTag nbtTags) {
        Map<Upgrade, Integer> upgrades = null;
        if (nbtTags != null && nbtTags.contains(SerializationConstants.UPGRADES, Tag.TAG_LIST)) {
            ListTag list = nbtTags.getList(SerializationConstants.UPGRADES, Tag.TAG_COMPOUND);
            for (int tagCount = 0; tagCount < list.size(); tagCount++) {
                CompoundTag compound = list.getCompound(tagCount);
                Upgrade upgrade = BY_ID.apply(compound.getInt(SerializationConstants.TYPE));
                int installed = Math.max(compound.getInt(SerializationConstants.AMOUNT), 0);
                if (installed > 0) {
                    if (upgrades == null) {
                        upgrades = new EnumMap<>(Upgrade.class);
                    }
                    upgrades.put(upgrade, installed);
                }
            }
        }
        return upgrades == null ? Collections.emptyMap() : upgrades;
    }

    public static void saveMap(Map<Upgrade, Integer> upgrades, CompoundTag nbtTags) {
        ListTag list = new ListTag();
        for (Map.Entry<Upgrade, Integer> entry : upgrades.entrySet()) {
            list.add(entry.getKey().getTag(entry.getValue()));
        }
        nbtTags.put(SerializationConstants.UPGRADES, list);
    }

    public CompoundTag getTag(int amount) {
        CompoundTag compound = new CompoundTag();
        compound.putInt(SerializationConstants.TYPE, ordinal());
        compound.putInt(SerializationConstants.AMOUNT, amount);
        return compound;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String getTranslationKey() {
        return langKey.getTranslationKey();
    }

    public Component getDescription() {
        return descLangKey.translate();
    }

    public int getMax() {
        return maxStack;
    }

    public EnumColor getColor() {
        return color;
    }

    public interface IUpgradeInfoHandler {

        List<Component> getInfo(Upgrade upgrade);
    }
}