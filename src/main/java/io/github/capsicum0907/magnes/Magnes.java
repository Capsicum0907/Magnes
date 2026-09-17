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

@Mod(Magnes.MODID)
public class Magnes {
    public static final String MODID = "magnes";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Magnes(IEventBus modEventBus, ModContainer modContainer) {
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

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().tickCount % MagnesConfig.TICK_INTERVAL.get() == 0) {
            Magnetism.sweep(event.getEntity());
        }
    }

    @net.neoforged.fml.common.Mod(value = MODID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
    public static class Client {
        public Client(IEventBus modEventBus, ModContainer modContainer) {
            modEventBus.addListener(io.github.capsicum0907.magnes.client.MagnesClient::registerProperties);
        }
    }
}