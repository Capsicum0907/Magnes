package io.github.capsicum0907.magnes;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import org.slf4j.Logger;

/**
 * Entry point. {@link #MODID} must match {@code mod_id} in gradle.properties,
 * which is what the generated neoforge.mods.toml is filled from.
 */
@Mod(Magnes.MODID)
public class Magnes {
    public static final String MODID = "magnes";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Magnes(IEventBus modEventBus, ModContainer modContainer) {
        // SERVER, not COMMON: where a dropped item is, is world state, and only the
        // server decides world state. A client gets the host's values.
        modContainer.registerConfig(ModConfig.Type.SERVER, MagnesConfig.SPEC);

        MagnesRegistry.ITEMS.register(modEventBus);
        MagnesRegistry.COMPONENTS.register(modEventBus);
        modEventBus.addListener(Magnes::addToCreativeTab);
        NeoForge.EVENT_BUS.addListener(Magnes::onPlayerTick);

        LOGGER.info("Magnes {} loaded.", modContainer.getModInfo().getVersion());
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(MagnesRegistry.MAGNET);
        }
    }

    /**
     * Post rather than Pre, so a sweep sees where the player finished the tick.
     *
     * <p>The interval is counted off the player's own age rather than the server's,
     * so players fall on different ticks instead of all sweeping on the same one.
     */
    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().tickCount % MagnesConfig.TICK_INTERVAL.get() == 0) {
            Magnetism.sweep(event.getEntity());
        }
    }
}
