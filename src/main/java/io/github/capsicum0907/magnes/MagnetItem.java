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

public class MagnetItem extends Item {
    public MagnetItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean isActive(ItemStack stack) {
        return stack.getItem() instanceof MagnetItem
                && stack.getOrDefault(MagnesRegistry.ACTIVE.get(), Boolean.TRUE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean now = !isActive(stack);
        stack.set(MagnesRegistry.ACTIVE.get(), now);

        level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS,
                0.6F, now ? 1.2F : 0.8F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        boolean active = isActive(stack);
        lines.add(Component.translatable(active ? "item.magnes.magnet.on" : "item.magnes.magnet.off")
                .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY));

        if (MagnesConfig.SPEC.isLoaded()) {
            lines.add(Component.translatable("item.magnes.magnet.reach", MagnesConfig.RADIUS.get())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
