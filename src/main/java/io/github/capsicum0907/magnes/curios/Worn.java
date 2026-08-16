package io.github.capsicum0907.magnes.curios;

import io.github.capsicum0907.magnes.MagnetItem;

import net.minecraft.world.entity.player.Player;

import top.theillusivec4.curios.api.CuriosApi;

/**
 * The whole of what Magnes knows about Curios: whether a magnet is being worn.
 *
 * <p>Nothing else. Curios is a place to keep a thing, and a magnet kept there works
 * exactly as one kept anywhere else — so the integration is one question, asked in one
 * place, and no behaviour of its own.
 *
 * <p>This class exists only when Curios does. It is in a package of its own for that
 * reason, and the check that guards it is in
 * {@link io.github.capsicum0907.magnes.Mods}, never here: calling a static method
 * initialises the class it is on, so a guard living in this file would load the very
 * thing it was meant to avoid.
 */
public final class Worn {
    private Worn() {
    }

    /** Whether any Curios slot holds a switched-on magnet. */
    public static boolean magnet(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(worn -> worn.findFirstCurio(MagnetItem::isActive))
                .isPresent();
    }
}
