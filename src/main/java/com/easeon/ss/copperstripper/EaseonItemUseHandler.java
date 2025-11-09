package com.easeon.ss.copperstripper;

import com.easeon.ss.core.helper.CopperHelper;
import com.easeon.ss.core.util.system.EaseonLogger;
import com.easeon.ss.core.wrapper.EaseonItem;
import com.easeon.ss.core.wrapper.EaseonPlayer;
import com.easeon.ss.core.wrapper.EaseonWorld;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EaseonItemUseHandler {
    private final static EaseonLogger logger = EaseonLogger.of();

    public static ActionResult onUseItem(ServerPlayerEntity playerEntity, World mcWorld, Hand hand) {
        var world = new EaseonWorld(mcWorld);
        if (world.isClient()) return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        var player = new EaseonPlayer(playerEntity);

        var mainHand = player.getMainHandStack();
        var offHand = player.getOffHandStack();

        var mainIsAxe = mainHand.of(AxeItem.class);
        var offIsAxe = offHand.of(AxeItem.class);
        var mainIsCopper = CopperHelper.isCopper(mainHand);
        var offIsCopper = CopperHelper.isCopper(offHand);

        if ((mainIsAxe && offIsCopper) || (offIsAxe && mainIsCopper)) {
            var axeStack = mainIsAxe ? mainHand : offHand;
            var copperStack = mainIsCopper ? mainHand : offHand;

            EaseonItem strippedCopper = null;
            var unwaxed = CopperHelper.removeWax(copperStack);
            if (unwaxed.isPresent()) {
                strippedCopper = unwaxed.get().easeonItem();
            } else {
                var deoxidized = CopperHelper.deoxidize(copperStack);
                if (deoxidized.isPresent()) {
                    strippedCopper = deoxidized.get().easeonItem();
                }
            }

            if (strippedCopper == null) return ActionResult.PASS;

            axeStack.damage(player);
            player.giveOrDropItem(strippedCopper, 1);
            player.removeItem(copperStack, 1);
            world.playSound(player.getPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.PLAYERS, 1.0f);

            if (mainIsAxe)
                player.swingHand(Hand.MAIN_HAND);
            else
                player.swingHand(Hand.OFF_HAND);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}