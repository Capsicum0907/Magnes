package io.github.capsicum0907.magnes;

import net.neoforged.fml.ModList;

// Must never name a Curios class: the guard cannot live behind the door it is guarding.
public final class Mods {
    private static final String CURIOS = "curios";

    private Mods() {
    }

    public static boolean curios() {
        return ModList.get().isLoaded(CURIOS);
    }
}
