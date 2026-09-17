package io.github.capsicum0907.magnes;

import java.util.function.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Magnetism {
    private Magnetism() {
    }

    public static void sweep(Player player) {
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator()) {
            return;
        }
        if (!MagnesConfig.ENABLED.get() || !isMagnetic(player)) {
            return;
        }

        Level level = player.level();
        AABB reach = player.getBoundingBox().inflate(MagnesConfig.RADIUS.get());

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, reach, free())) {
            bring(player, item);
        }
        if (MagnesConfig.INCLUDES_EXPERIENCE.get()) {
            for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, reach, Entity::isAlive)) {
                bring(player, orb);
            }
        }
    }

    private static Predicate<ItemEntity> free() {
        return item -> item.isAlive() && !item.hasPickUpDelay();
    }

    private static void bring(Player player, Entity entity) {
        if (MagnesConfig.PULL.get() == MagnesConfig.Pull.TAKEN) {
            entity.playerTouch(player);
            return;
        }

        Vec3 toward = player.getEyePosition().subtract(entity.position());
        if (toward.lengthSqr() < 1.0e-4) {
            entity.playerTouch(player);
            return;
        }
        entity.setDeltaMovement(toward.normalize().scale(MagnesConfig.DRAWN_SPEED.get()));
        entity.hasImpulse = true;
    }

    private static boolean isMagnetic(Player player) {
        if (!MagnesConfig.NEEDS_MAGNET.get()) {
            return true;
        }
        if (MagnetItem.isActive(player.getOffhandItem()) || MagnetItem.isActive(player.getMainHandItem())) {
            return true;
        }
        if (Mods.curios() && io.github.capsicum0907.magnes.curios.Worn.magnet(player)) {
            return true;
        }
        if (MagnesConfig.MUST_BE_HELD.get()) {
            return false;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (MagnetItem.isActive(stack)) {
                return true;
            }
        }
        return false;
    }
}
