package io.github.capsicum0907.magnes.client;

import io.github.capsicum0907.magnes.Magnes;
import io.github.capsicum0907.magnes.MagnesRegistry;
import io.github.capsicum0907.magnes.MagnetItem;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * The client half: one number, so that a model can be picked from it.
 *
 * <p>The property has to be registered here rather than declared in the model,
 * because a model override reads a number off the stack and something has to say
 * where that number comes from.
 */
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
