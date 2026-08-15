package io.github.capsicum0907.magnes;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * The magnet. It carries no behaviour of its own: {@link Magnetism} looks for one
 * in the player's inventory each tick and does the work. What lives here is the
 * switch, and saying which way the switch is set.
 */
public class MagnetItem extends Item {
    public MagnetItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /** Absent means on, so a magnet that has never been switched works. */
    public static boolean isActive(ItemStack stack) {
        return stack.getItem() instanceof MagnetItem
                && stack.getOrDefault(MagnesRegistry.ACTIVE.get(), Boolean.TRUE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean now = !isActive(stack);
        stack.set(MagnesRegistry.ACTIVE.get(), now);

        // Pitched up for on and down for off, so the two are told apart without looking.
        level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS,
                0.6F, now ? 1.2F : 0.8F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** The state has to be visible from the inventory, where the item does its work. */
    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        boolean active = isActive(stack);
        lines.add(Component.translatable(active ? "item.magnes.magnet.on" : "item.magnes.magnet.off")
                .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY));
    }
}
