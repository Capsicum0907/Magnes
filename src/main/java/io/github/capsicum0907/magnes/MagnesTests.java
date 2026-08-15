package io.github.capsicum0907.magnes;

import io.github.capsicum0907.magnes.data.TestStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * What a magnet does and, more to the point, what it leaves alone.
 *
 * <p>Three of these are refusals. A magnet that takes too much is worse than one
 * that takes too little: a player who cannot drop an item, or whose neighbour's
 * death pile walks away, has a bug they cannot work around.
 *
 * <p>Run with {@code gradlew runGameTestServer}.
 */
@GameTestHolder(Magnes.MODID)
@PrefixGameTestTemplate(false)
// makeMockServerPlayerInLevel is marked for removal and has no replacement offered.
// A plain mock player is not a ServerPlayer, and an experience orb only gives itself
// to one of those, so there is nothing else to test the experience half with. When it
// goes, this is the line that has to be answered.
@SuppressWarnings("removal")
public final class MagnesTests {
    /** The middle of the floor, with room in every direction. */
    private static final BlockPos STANDING = new BlockPos(10, 1, 10);

    /** Well inside the default reach of six blocks. */
    private static final int NEAR = 2;

    /** Well outside it, and still on the floor. */
    private static final int FAR = 9;

    private MagnesTests() {
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void takesItemsWithinReach(GameTestHelper helper) {
        ServerPlayer player = playerWithMagnet(helper);
        ItemEntity item = drop(helper, NEAR);

        Magnetism.sweep(player);

        check(item.isRemoved(), "an item two blocks away should have been taken");
        check(player.getInventory().contains(new ItemStack(Items.DIAMOND)),
                "the item that was taken should be in the inventory");
        helper.succeed();
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void leavesItemsBeyondReach(GameTestHelper helper) {
        ServerPlayer player = playerWithMagnet(helper);
        ItemEntity item = drop(helper, FAR);

        Magnetism.sweep(player);

        check(!item.isRemoved(), "an item nine blocks away is outside the reach and should be left");
        helper.succeed();
    }

    /**
     * The pickup delay is what makes dropping something possible. Without honouring
     * it a magnet would take back everything the player threw, the instant they
     * threw it — and would reach into a stranger's fresh death pile.
     */
    @GameTest(template = TestStructures.FLOOR)
    public static void leavesItemsJustDropped(GameTestHelper helper) {
        ServerPlayer player = playerWithMagnet(helper);
        ItemEntity item = drop(helper, NEAR);
        item.setDefaultPickUpDelay();

        Magnetism.sweep(player);

        check(!item.isRemoved(), "an item still inside its pickup delay should be left");
        helper.succeed();
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void takesExperience(GameTestHelper helper) {
        ServerPlayer player = playerWithMagnet(helper);
        ExperienceOrb orb = new ExperienceOrb(helper.getLevel(), 0.0, 0.0, 0.0, 7);
        orb.setPos(at(helper, NEAR));
        helper.getLevel().addFreshEntity(orb);

        Magnetism.sweep(player);

        check(orb.isRemoved(), "an experience orb within reach should have been taken");
        check(player.totalExperience > 0, "taking an orb should have given the player experience");
        helper.succeed();
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void doesNothingSwitchedOff(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(at(helper, 0));
        ItemStack magnet = new ItemStack(MagnesRegistry.MAGNET.get());
        magnet.set(MagnesRegistry.ACTIVE.get(), false);
        player.getInventory().add(magnet);
        ItemEntity item = drop(helper, NEAR);

        Magnetism.sweep(player);

        check(!item.isRemoved(), "a magnet that is switched off should take nothing");
        helper.succeed();
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void doesNothingWithoutAMagnet(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(at(helper, 0));
        ItemEntity item = drop(helper, NEAR);

        Magnetism.sweep(player);

        check(!item.isRemoved(), "a player carrying no magnet should take nothing");
        helper.succeed();
    }

    private static ServerPlayer playerWithMagnet(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(at(helper, 0));
        player.getInventory().add(new ItemStack(MagnesRegistry.MAGNET.get()));
        return player;
    }

    private static ItemEntity drop(GameTestHelper helper, int blocksAway) {
        Vec3 where = at(helper, blocksAway);
        ItemEntity item = new ItemEntity(helper.getLevel(), where.x, where.y, where.z,
                new ItemStack(Items.DIAMOND));
        item.setPickUpDelay(0);
        helper.getLevel().addFreshEntity(item);
        return item;
    }

    /** A point on the floor, that many blocks along z from where the player stands. */
    private static Vec3 at(GameTestHelper helper, int blocksAway) {
        return Vec3.atBottomCenterOf(helper.absolutePos(STANDING.offset(0, 0, blocksAway)));
    }

    private static void check(boolean condition, String expectation) {
        if (!condition) {
            throw new GameTestAssertException(expectation);
        }
    }
}
