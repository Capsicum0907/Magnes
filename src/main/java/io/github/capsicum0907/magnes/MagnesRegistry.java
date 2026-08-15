package io.github.capsicum0907.magnes;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registration. One item and one component; there is not enough here to split up. */
public final class MagnesRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Magnes.MODID);
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Magnes.MODID);

    /**
     * Whether this magnet is switched on.
     *
     * <p>Absent means on. A magnet that has never been touched should work, and
     * storing the state only once it differs from that default keeps a fresh one
     * free of components — so two of them still stack.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVE =
            COMPONENTS.registerComponentType("active",
                    builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredItem<MagnetItem> MAGNET = ITEMS.registerItem("magnet", MagnetItem::new);

    private MagnesRegistry() {
    }
}
