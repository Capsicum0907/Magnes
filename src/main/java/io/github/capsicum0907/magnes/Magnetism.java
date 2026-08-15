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

/**
 * The whole of what the mod does, in one call.
 *
 * <p>It is a plain method rather than the body of an event handler so that a test
 * can run one sweep and look at the result, instead of having to arrange for a tick
 * to happen to a real player.
 *
 * <p><b>Nothing here reimplements picking something up.</b> Both an item and an
 * experience orb already know how to give themselves to a player — through
 * {@code playerTouch}, which handles the pickup delay, whose the item is, the sound,
 * the advancement, the pickup events other mods listen for, and, for an orb, mending
 * before experience. Calling it is the difference between this mod having opinions
 * about pickup and having none.
 */
public final class Magnetism {
    private Magnetism() {
    }

    /** One sweep for one player. Safe to call every tick; does nothing without a magnet. */
    public static void sweep(Player player) {
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator()) {
            return;
        }
        if (!MagnesConfig.ENABLED.get() || !carriesActiveMagnet(player)) {
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

    /**
     * An item still inside its pickup delay is left alone. That delay is what makes
     * dropping something possible at all: without honouring it, a magnet would take
     * back everything the player threw, the instant they threw it. It is also what
     * keeps another player's fresh death drop out of reach.
     */
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
            entity.playerTouch(player); // close enough that a direction would be noise
            return;
        }
        entity.setDeltaMovement(toward.normalize().scale(MagnesConfig.DRAWN_SPEED.get()));
        entity.hasImpulse = true;
    }

    private static boolean carriesActiveMagnet(Player player) {
        if (MagnetItem.isActive(player.getOffhandItem()) || MagnetItem.isActive(player.getMainHandItem())) {
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
