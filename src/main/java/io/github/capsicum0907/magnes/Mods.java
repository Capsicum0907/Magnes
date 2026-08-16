package io.github.capsicum0907.magnes;

import net.neoforged.fml.ModList;

/**
 * Which optional mods are here.
 *
 * <p>A class that <b>must never name anything belonging to them</b>, which is the
 * whole reason it exists rather than being a method on the thing it guards. Calling a
 * static method initialises the class it is on, so a check living next to the code it
 * protects loads exactly what it was meant to avoid — and the mod dies during
 * construction with {@code NoClassDefFoundError}. <b>The guard cannot live behind the
 * door it is guarding.</b>
 *
 * <p>Every use is a short-circuit: the right-hand side is only reached when the answer
 * is yes, and a method body that mentions a missing class is fine as long as it is
 * never run.
 */
public final class Mods {
    private static final String CURIOS = "curios";

    private Mods() {
    }

    /** Whether there are slots to wear a magnet in. */
    public static boolean curios() {
        return ModList.get().isLoaded(CURIOS);
    }
}
