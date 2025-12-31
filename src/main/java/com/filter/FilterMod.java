package com.filter;

import com.filter.client.FilterClientEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

/**
 * @author cshisan
 */
@Mod(FilterMod.MOD_ID)
public class FilterMod {
    public static final String MOD_ID = "filter";

    public FilterMod(IEventBus modEventBus, ModContainer container) {

        NeoForge.EVENT_BUS.addListener(FilterPickupHandler::onItemPickupPre);
        NeoForge.EVENT_BUS.addListener(FilterCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(FilterNetwork::onPlayerLogin);
        modEventBus.addListener(FilterNetwork::onRegisterPayloadHandlers);
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(FilterClientEvents::onScreenInit);
            NeoForge.EVENT_BUS.addListener(FilterClientEvents::onMouseReleasedPre);
            NeoForge.EVENT_BUS.addListener(FilterClientEvents::onScreenRenderPre);
            NeoForge.EVENT_BUS.addListener(FilterClientEvents::onMouseScrolledPre);
        }

    }
}
