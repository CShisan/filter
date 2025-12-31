package com.filter.common.utils;

import net.minecraft.client.Minecraft;

/**
 * @author cshisan
 */
public class MouseHelper {
    private static final Minecraft MC = Minecraft.getInstance();

    public static double winX() {
        return MC.mouseHandler.xpos();
    }

    public static double winY() {
        return MC.mouseHandler.ypos();
    }

    public static double guiX() {
        return calcGui(winX());
    }

    public static double guiY() {
        return calcGui(winY());
    }

    public static double calcGui(double value) {
        return value * MC.getWindow().getGuiScaledWidth() / MC.getWindow().getWidth();
    }
}
