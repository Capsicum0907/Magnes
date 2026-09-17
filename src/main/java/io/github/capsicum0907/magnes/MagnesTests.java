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
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Magnes.MODID)
@PrefixGameTestTemplate(false)
// makeMockServerPlayerInLevel has no replacement, and an orb only gives itself to a ServerPlayer.
@SuppressWarnings("removal")
public final class MagnesTests {
    private static final BlockPos STANDING = new BlockPos(10, 1, 10);

    private static final int NEAR = 2;

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
        double was = MagnesConfig.RADIUS.get();
        try {
            // Set, not assumed: the config this runs against may have a different reach.
            MagnesConfig.RADIUS.set(6.0);
            ServerPlayer player = playerWithMagnet(helper);
            ItemEntity item = drop(helper, FAR);

            Magnetism.sweep(player);

            check(!item.isRemoved(), "an item nine blocks from a six block reach should be left");
            helper.succeed();
        } finally {
            MagnesConfig.RADIUS.set(was);
        }
    }

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

    @GameTest(template = TestStructures.FLOOR)
    public static void everyoneIsMagneticWhenTheItemIsNotNeeded(GameTestHelper helper) {
        boolean was = MagnesConfig.NEEDS_MAGNET.get();
        try {
            MagnesConfig.NEEDS_MAGNET.set(false);
            ServerPlayer player = helper.makeMockServerPlayerInLevel();
            player.setPos(at(helper, 0));
            ItemEntity item = drop(helper, NEAR);

            Magnetism.sweep(player);

            check(item.isRemoved(), "with needsMagnet off, an empty-handed player should still pull");
            helper.succeed();
        } finally {
            MagnesConfig.NEEDS_MAGNET.set(was);
        }
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void theTickReachesTheSweep(GameTestHelper helper) {
        ServerPlayer player = playerWithMagnet(helper);
        ItemEntity item = drop(helper, NEAR);

        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));

        check(item.isRemoved(), "a player tick should have reached the sweep and taken the item");
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

    private static Vec3 at(GameTestHelper helper, int blocksAway) {
        return Vec3.atBottomCenterOf(helper.absolutePos(STANDING.offset(0, 0, blocksAway)));
    }

    @GameTest(template = TestStructures.FLOOR)
    public static void aMagnetCanBeWorn(GameTestHelper helper) {
        ItemStack magnet = new ItemStack(MagnesRegistry.MAGNET.get());
        for (String slot : new String[] { "charm", "curio" }) {
            TagKey<Item> fits = TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("curios", slot));
            check(magnet.is(fits), "a magnet should be allowed in the " + slot + " slot");
        }
        helper.succeed();
    }

    private static void check(boolean condition, String expectation) {
        if (!condition) {
            throw new GameTestAssertException(expectation);
        }
    }
}
