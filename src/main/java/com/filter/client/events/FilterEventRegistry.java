package com.filter.client.events;

import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 界面模块注册表
 *
 * @author cshisan
 */
public final class FilterEventRegistry {
    private static final List<FilterEventModule> MODULES = new ArrayList<>();

    static {
        MODULES.add(new FilterPanelModule());
    }

    public static void onInit(ScreenEvent.Init.Post event) {
        for (FilterEventModule module : MODULES) {
            module.onInit(event);
        }
    }

    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        for (FilterEventModule module : MODULES) {
            module.onRenderPre(event);
        }
    }

    public static void onMouseReleasedPre(ScreenEvent.MouseButtonReleased.Pre event) {
        for (FilterEventModule module : MODULES) {
            module.onMouseReleasedPre(event);
        }
    }

    public static void onMouseScrolledPre(ScreenEvent.MouseScrolled.Pre event) {
        for (FilterEventModule module : MODULES) {
            module.onMouseScrolledPre(event);
        }
    }
}
