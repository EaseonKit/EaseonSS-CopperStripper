package com.easeon.ss.copperstripper;

import com.easeon.ss.core.api.common.base.BaseToggleModule;
import com.easeon.ss.core.api.definitions.enums.EventPhase;
import com.easeon.ss.core.api.events.EaseonItemUse;
import com.easeon.ss.core.api.events.EaseonItemUse.ItemUseTask;
import com.easeon.ss.core.helper.CopperHelper;
import net.fabricmc.api.ModInitializer;

public class Easeon extends BaseToggleModule implements ModInitializer {
    private ItemUseTask task;

    @Override
    public void onInitialize() {
        CopperHelper.init();
        logger.info("Initialized!");
    }

    public void updateTask() {
        if (config.enabled && task == null) {
            task = EaseonItemUse.register(EventPhase.BEFORE, EaseonItemUseHandler::onUseItem);
        }
        if (!config.enabled && task != null) {
            EaseonItemUse.unregister(task);
            task = null;
        }
    }
}