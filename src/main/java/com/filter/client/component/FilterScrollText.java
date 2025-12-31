package com.filter.client.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author cshisan
 */
public interface FilterScrollText {
    int ID_SCROLL_DELAY_MS = 2000;
    int ID_SCROLL_GAP = 40;
    float ID_SCROLL_SPEED_PX_PER_SEC = 20f;
    Font FONT = Minecraft.getInstance().font;
    long START_TIME = FilterScrollText.now();

    /**
     * 滚动文字
     *
     * @param guiGraphics guiGraphics
     * @param text        text
     * @param x           x
     * @param y           y
     * @param rowY        行y坐标
     * @param color       文字颜色
     * @param clipRight   裁剪右边界
     * @param lineHeight  行高
     */
    static void draw(GuiGraphics guiGraphics, String text, int x, int y, int rowY, int color, int clipRight, int lineHeight) {
        int maxWidth = Math.max(0, clipRight - x);
        int textWidth = FONT.width(text);

        // 画固定文字
        if (textWidth <= maxWidth || maxWidth == 0) {
            guiGraphics.drawString(FONT, text, x, y, color, false);
        }
        // 画滚动文字
        else {
            guiGraphics.enableScissor(x, rowY, clipRight, rowY + lineHeight - 1);
            long elapsed = FilterScrollText.now() - START_TIME;
            long scrollTime = Math.max(0, elapsed - ID_SCROLL_DELAY_MS);
            float distance = textWidth + ID_SCROLL_GAP;
            float scrollDurationMs = (distance / ID_SCROLL_SPEED_PX_PER_SEC) * 1000f;
            float cycleMs = scrollDurationMs + ID_SCROLL_DELAY_MS;
            float phase = (float) (scrollTime % (long) cycleMs);
            float scroll = phase < scrollDurationMs ? phase * (ID_SCROLL_SPEED_PX_PER_SEC / 1000f) : distance;
            int drawX = Math.round(x - scroll);
            guiGraphics.drawString(FONT, text, drawX, y, color, false);
            guiGraphics.drawString(FONT, text, drawX + textWidth + ID_SCROLL_GAP, y, color, false);
            guiGraphics.disableScissor();
        }
    }

    /**
     * 当前秒数
     *
     * @return long
     */
    private static long now() {
        return LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
