package io.github.capsicum0907.magnes;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Every tunable value lives here. Nothing else in the mod may hold a literal.
 *
 * <p>SERVER, not COMMON: where a dropped item is, is world state, and only the
 * server decides world state. A client gets the host's values.
 *
 * <p>These can be settings at all — unlike a tool's reach, which is baked into a
 * data component when the item is registered — because every one of them is read
 * on the tick it is used.
 */
public final class MagnesConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** How something within reach gets to the player. */
    public enum Pull {
        /** Straight into the inventory. Certain, and undramatic. */
        TAKEN,
        /** Pushed toward the player, to be picked up the ordinary way. Better to watch, and it can miss. */
        DRAWN
    }

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Master switch. When off, nothing is pulled to anybody, whatever they carry.",
                    "On does not mean everybody is magnetic - see needsMagnet just below.")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue NEEDS_MAGNET = BUILDER
            .comment("Whether a player has to be carrying the magnet item for any of this to happen.",
                    "On: the magnet is a thing you make and hold. Off: every player is magnetic,",
                    "always, and the item is decoration.",
                    "This is the setting somebody looking for 'why is nothing being picked up'",
                    "actually wants, so it is named after the question rather than after the code.")
            .define("needsMagnet", true);

    public static final ModConfigSpec.DoubleValue RADIUS = BUILDER
            .comment("How far a magnet reaches, in blocks, measured from the player.")
            .defineInRange("radius", 6.0, 1.0, 32.0);

    public static final ModConfigSpec.EnumValue<Pull> PULL = BUILDER
            .comment("How something within reach gets to the player.",
                    "TAKEN - straight into the inventory",
                    "DRAWN - pushed toward the player, then picked up as usual")
            .defineEnum("pull", Pull.TAKEN);

    public static final ModConfigSpec.DoubleValue DRAWN_SPEED = BUILDER
            .comment("How fast DRAWN moves things, in blocks per tick. Ignored by TAKEN.")
            .defineInRange("drawnSpeed", 0.35, 0.05, 2.0);

    public static final ModConfigSpec.BooleanValue INCLUDES_EXPERIENCE = BUILDER
            .comment("Whether experience orbs are pulled as well as items.",
                    "Orbs are absorbed at the rate the game absorbs them when walked over,",
                    "which is one every other tick. A magnet gathers them; it does not hurry them.")
            .define("includesExperience", true);

    public static final ModConfigSpec.BooleanValue MUST_BE_HELD = BUILDER
            .comment("Whether a magnet only works in a hand.",
                    "Off by default: the point of the item is not having to hold it.")
            .define("mustBeHeld", false);

    public static final ModConfigSpec.IntValue TICK_INTERVAL = BUILDER
            .comment("How many ticks between sweeps. 1 is every tick.",
                    "Raising it costs smoothness under DRAWN and buys back scanning time",
                    "on a server with many players.")
            .defineInRange("tickInterval", 1, 1, 40);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private MagnesConfig() {
    }
}
