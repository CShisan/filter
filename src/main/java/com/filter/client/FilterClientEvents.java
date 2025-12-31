package com.filter.client;

import com.filter.client.events.FilterEventRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
public final class FilterClientEvents {
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        FilterEventRegistry.onInit(event);
    }

    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        FilterEventRegistry.onRenderPre(event);
    }

    public static void onMouseReleasedPre(ScreenEvent.MouseButtonReleased.Pre event) {
        FilterEventRegistry.onMouseReleasedPre(event);
    }

    public static void onMouseScrolledPre(ScreenEvent.MouseScrolled.Pre event) {
        FilterEventRegistry.onMouseScrolledPre(event);
    }
}
