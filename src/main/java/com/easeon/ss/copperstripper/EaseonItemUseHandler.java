package com.easeon.ss.copperstripper;

import com.easeon.ss.core.helper.CopperHelper;
import com.easeon.ss.core.util.system.EaseonLogger;
import com.easeon.ss.core.wrapper.*;
import net.minecraft.item.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import java.util.Optional;

public class EaseonItemUseHandler {
    private final static EaseonLogger logger = EaseonLogger.of();

    public static ActionResult onUseItem(EaseonWorld world, EaseonPlayer player, Hand hand) {
        if (world.isClient() || hand != Hand.MAIN_HAND)
            return ActionResult.PASS;

        final var main = player.getMainHandStack();
        final var off = player.getOffHandStack();

        EaseonItem tool;
        EaseonItem copper;
        Hand swing = Hand.MAIN_HAND;
        if (main.of(AxeItem.class) && CopperHelper.isCopper(off)) {
            tool = main;
            copper = off;
        }
        else if (off.of(AxeItem.class) && CopperHelper.isCopper(main)) {
            tool = off;
            copper = main;
            swing = Hand.OFF_HAND;
        }
        else {
            return ActionResult.PASS;
        }

        final var reward = processCopper(copper).orElse(null);
        if (reward == null)
            return ActionResult.PASS;

        tool.damage(player);
        player.giveOrDropItem(reward.easeonItem(), 1);
        player.removeItem(copper, 1);
        world.playSound(player.getPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.PLAYERS, 1.0f);
        player.swingHand(swing);

        return ActionResult.SUCCESS;
    }

    private static Optional<EaseonBlock> processCopper(EaseonItem copper) {
        return CopperHelper.removeWax(copper).or(() -> CopperHelper.deoxidize(copper));
    }
}