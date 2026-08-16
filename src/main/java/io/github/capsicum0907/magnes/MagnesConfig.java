package io.github.capsicum0907.magnes;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Every tunable value lives here. Nothing else in the mod may hold a literal.
 *
 * <p>SERVER, not COMMON: where a dropped item is, is world state, and only the
 * server decides world state. A client gets the host's values.
 *
 * <p>These can be settings at all — unlike a tool's reach, which is baked into a
 * data component when the item is registered — because every one of them is read on
 * the tick it is used.
 *
 * <p>Grouped into sections, with the comments kept short. A config file is read
 * while looking for one setting rather than read through, and the reasoning behind
 * these choices belongs in the README.
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
            .comment("Master switch. Off means nothing is pulled to anybody, whatever they carry.")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue NEEDS_MAGNET = BUILDER
            .comment("Who this happens to.")
            .push("magnet")
            .comment("Whether a player has to be carrying the magnet item.",
                    "Off makes every player magnetic, always, and the item decoration.")
            .define("needed", true);

    public static final ModConfigSpec.BooleanValue MUST_BE_HELD = BUILDER
            .comment("Whether it only works in a hand, rather than anywhere in the inventory.")
            .define("mustBeHeld", false);

    public static final ModConfigSpec.DoubleValue RADIUS = BUILDER.pop()
            .comment("What is pulled, and how far.")
            .push("pull")
            .comment("How far a magnet reaches, in blocks.")
            .defineInRange("radius", 6.0, 1.0, 32.0);

    public static final ModConfigSpec.EnumValue<Pull> PULL = BUILDER
            .comment("TAKEN puts things straight into the inventory.",
                    "DRAWN pushes them toward the player, to be picked up as usual.")
            .defineEnum("mode", Pull.TAKEN);

    public static final ModConfigSpec.DoubleValue DRAWN_SPEED = BUILDER
            .comment("Blocks per tick under DRAWN. Ignored by TAKEN.")
            .defineInRange("drawnSpeed", 0.35, 0.05, 2.0);

    public static final ModConfigSpec.BooleanValue INCLUDES_EXPERIENCE = BUILDER
            .comment("Whether experience orbs are pulled as well as items.",
                    "Orbs are absorbed at the rate the game absorbs them when walked over.")
            .define("includesExperience", true);

    public static final ModConfigSpec.IntValue TICK_INTERVAL = BUILDER.pop()
            .comment("What a busy server might want to turn down.")
            .push("cost")
            .comment("Ticks between sweeps. 1 is every tick.",
                    "Raising it costs smoothness under DRAWN and buys back scanning time.")
            .defineInRange("tickInterval", 1, 1, 40);

    public static final ModConfigSpec SPEC = BUILDER.pop().build();

    private MagnesConfig() {
    }
}
