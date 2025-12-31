package com.filter.client.component;

import net.minecraft.client.gui.components.AbstractWidget;

/**
 * @author cshisan
 */
public interface FilterComponent {
    /**
     * 显示
     *
     * @param visible visible
     */
    void visible(boolean visible);

    /**
     * 显示
     *
     * @param widgets 组件
     * @param visible visible
     */
    static void visible(boolean visible, FilterComponent... widgets) {
        for (FilterComponent widget : widgets) {
            widget.visible(visible);
        }
    }

    /**
     * 刷新
     *
     * @param widget 组件
     * @param x      x
     * @param y      y
     */
    static void refresh(AbstractWidget widget, int x, int y) {
        widget.setX(x);
        widget.setY(y);
    }

    /**
     * 是否在区域内
     *
     * @param widget widget
     * @param mouseX mouseX
     * @param mouseY mouseY
     * @return boolean
     */
    static boolean isInArea(AbstractWidget widget, double mouseX, double mouseY) {
        return isInArea(
                widget.getY(), widget.getX(),
                widget.getY() + widget.getHeight(),
                widget.getX() + widget.getWidth(),
                mouseX, mouseY
        );
    }

    /**
     * 是否在区域内
     *
     * @param top    top
     * @param bottom bottom
     * @param left   left
     * @param right  right
     * @param mouseX mouseX
     * @param mouseY mouseY
     * @return boolean
     */
    static boolean isInArea(double top, double left, double bottom, double right, double mouseX, double mouseY) {
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    /**
     * 计算坐标值
     *
     * @param x x
     * @param y y
     * @return long
     */
    static long calc(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }
}
