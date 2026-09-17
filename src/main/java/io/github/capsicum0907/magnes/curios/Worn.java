package io.github.capsicum0907.magnes.curios;

import io.github.capsicum0907.magnes.MagnetItem;

import net.minecraft.world.entity.player.Player;

import top.theillusivec4.curios.api.CuriosApi;

public final class Worn {
    private Worn() {
    }

    public static boolean magnet(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(worn -> worn.findFirstCurio(MagnetItem::isActive))
                .isPresent();
    }
}
