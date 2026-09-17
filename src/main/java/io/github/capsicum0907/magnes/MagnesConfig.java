package io.github.capsicum0907.magnes;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MagnesConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public enum Pull {
        TAKEN,
        DRAWN
    }

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue NEEDS_MAGNET = BUILDER
            .push("magnet")
            .define("needed", true);

    public static final ModConfigSpec.BooleanValue MUST_BE_HELD = BUILDER
            .define("mustBeHeld", false);

    public static final ModConfigSpec.DoubleValue RADIUS = BUILDER.pop()
            .push("pull")
            .defineInRange("radius", 6.0, 1.0, 32.0);

    public static final ModConfigSpec.EnumValue<Pull> PULL = BUILDER
            .defineEnum("mode", Pull.TAKEN);

    public static final ModConfigSpec.DoubleValue DRAWN_SPEED = BUILDER
            .defineInRange("drawnSpeed", 0.35, 0.05, 2.0);

    public static final ModConfigSpec.BooleanValue INCLUDES_EXPERIENCE = BUILDER
            .define("includesExperience", true);

    public static final ModConfigSpec.IntValue TICK_INTERVAL = BUILDER.pop()
            .push("cost")
            .defineInRange("tickInterval", 1, 1, 40);

    public static final ModConfigSpec SPEC = BUILDER.pop().build();

    private MagnesConfig() {
    }
}
