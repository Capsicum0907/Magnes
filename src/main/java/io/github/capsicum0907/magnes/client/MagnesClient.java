package io.github.capsicum0907.magnes.client;

import io.github.capsicum0907.magnes.Magnes;
import io.github.capsicum0907.magnes.MagnesRegistry;
import io.github.capsicum0907.magnes.MagnetItem;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class MagnesClient {
    private MagnesClient() {
    }

    public static void registerProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                MagnesRegistry.MAGNET.get(),
                ResourceLocation.fromNamespaceAndPath(Magnes.MODID, "active"),
                (stack, level, entity, seed) -> MagnetItem.isActive(stack) ? 1.0F : 0.0F));
    }
}
