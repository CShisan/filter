package com.filter.client.events;

import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * 统一的界面模块入口
 *
 * @author cshisan
 */
public interface FilterEventModule {
    /**
     * 界面初始化完成后调用
     *
     * @param event 事件对象
     */
    default void onInit(ScreenEvent.Init.Post event) {
    }

    /**
     * 界面渲染前调用
     *
     * @param event 事件对象
     */
    default void onRenderPre(ScreenEvent.Render.Pre event) {
    }

    /**
     * 鼠标按键释放前调用
     *
     * @param event 事件对象
     */
    default void onMouseReleasedPre(ScreenEvent.MouseButtonReleased.Pre event) {
    }

    /**
     * 鼠标滚轮滚动前调用
     *
     * @param event 事件对象
     */
    default void onMouseScrolledPre(ScreenEvent.MouseScrolled.Pre event) {
    }
}
