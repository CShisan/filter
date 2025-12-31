package com.filter.client.component;

import com.filter.client.FilterClientCommands;
import com.filter.client.FilterClientState;
import com.filter.client.model.MarkMeta;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
final class FilterMark extends ScrollPanel implements FilterComponent, FilterScrollText {
    private static final int LINE_HEIGHT = 20;
    private boolean visible = true;

    FilterMark(int width, int height, int top, int left) {
        super(Minecraft.getInstance(), width, height, top, left);
    }

    private boolean handleRowClick(double mouseY, int button) {
        if (button == 0) {
            int index = (int) ((mouseY - (this.top + 2) + this.scrollDistance) / LINE_HEIGHT);
            List<MarkMeta> entries = FilterClientState.metas();
            if (index < 0 || index >= entries.size()) {
                return false;
            }
            MarkMeta meta = entries.get(index);
            FilterClientCommands.unmark(meta.id(), meta.tag());
            FilterClientState.removeMeta(meta.id());
            return true;
        }
        return false;
    }

    private boolean isInListArea(double mouseX, double mouseY) {
        double left = this.left + 2;
        double right = this.left + this.width - 6;
        double top = this.top + 2;
        double bottom = this.top + this.height - 2;
        return FilterComponent.isInArea(top, left, bottom, right, mouseX, mouseY);
    }

    private boolean isRowHovered(int mouseX, int mouseY, int rowY) {
        return isInListArea(mouseX, mouseY) && mouseY >= rowY && mouseY < rowY + LINE_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (visible) {
            if (isInListArea(mouseX, mouseY)) {
                return handleRowClick(mouseY, button);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xDelta, double yDelta) {
        if (visible) {
            if (getContentHeight() <= this.height - this.border) {
                return false;
            }
            return super.mouseScrolled(mouseX, mouseY, xDelta, yDelta);
        }
        return false;
    }

    @Override
    protected int getContentHeight() {
        int lines = FilterClientState.metas().size();
        return Math.max(1, lines) * LINE_HEIGHT + 4;
    }

    @Override
    protected void drawPanel(@NotNull GuiGraphics guiGraphics, int entryRight, int relativeY, @NotNull Tesselator tess, int mouseX, int mouseY) {
        List<MarkMeta> metas = FilterClientState.metas();
        int listLeft = this.left + 2;
        int listTop = this.top + 2;
        int listRight = this.left + this.width - 6;
        int listBottom = this.top + this.height - 2;
        guiGraphics.enableScissor(listLeft, listTop, listRight, listBottom);
        int y = relativeY + 2;
        for (MarkMeta meta : metas) {
            if (y + LINE_HEIGHT < listTop || y > listBottom) {
                y += LINE_HEIGHT;
                continue;
            }
            if (isRowHovered(mouseX, mouseY, y)) {
                guiGraphics.fill(this.left + 2, y, this.left + this.width - 6, y + LINE_HEIGHT - 1, 0x33FFFFFF);
            }
            if (meta.tag()) {
                if (meta.icon() != ItemStack.EMPTY) {
                    guiGraphics.renderItem(meta.icon(), this.left + 4, y + 2);
                }
                FilterScrollText.draw(
                        guiGraphics, "#" + meta.id(),
                        this.left + 24, y + 4, y, 0x7A7A7A,
                        this.left + this.width - 8, LINE_HEIGHT
                );
            } else {
                ItemStack stack = meta.icon();
                guiGraphics.renderItem(stack, this.left + 4, y + 2);
                guiGraphics.drawString(FONT, stack.getHoverName(), this.left + 24, y + 2, 0xE0E0E0, false);
                FilterScrollText.draw(
                        guiGraphics, meta.id().toString(),
                        this.left + 24, y + 11, y, 0x7A7A7A,
                        this.left + this.width - 8, LINE_HEIGHT
                );
            }
            y += LINE_HEIGHT;
        }
        if (metas.isEmpty()) {
            guiGraphics.drawString(FONT, "未标记任何标签", this.left + 4, y + 4, 0x7A7A7A, false);
        }
        guiGraphics.disableScissor();
    }

    @Override
    protected void drawBackground(@NotNull GuiGraphics guiGraphics, @NotNull Tesselator tess, float partialTick) {
        // 不画背景,重写取消父类背景填充
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        if (visible && this.getFocused() != null) {
            return NarrationPriority.FOCUSED;
        }
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        int count = FilterClientState.metas().size();
        narrationElementOutput.add(NarratedElementType.TITLE, Component.literal("标记列表"));
        narrationElementOutput.add(NarratedElementType.USAGE, Component.literal("共有 " + count + " 项，点击条目可取消标记"));
    }

    @Override
    public void visible(boolean visible) {
        this.visible = visible;
    }
}
