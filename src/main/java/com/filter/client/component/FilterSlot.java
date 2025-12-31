package com.filter.client.component;

import com.filter.client.FilterClientState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
final class FilterSlot extends AbstractWidget implements FilterComponent {
    private final AbstractContainerScreen<?> screen;
    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    FilterSlot(AbstractContainerScreen<?> screen, int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.literal("标记框"));
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int color = this.isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF777777;
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color);
        guiGraphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0xFF222222);
        ItemStack stack = FilterClientState.getPendingStack();
        if (!stack.isEmpty()) {
            guiGraphics.renderItem(stack, getX() + 1, getY() + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 && button != 1) {
            return false;
        }
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        if (button == 1) {
            FilterClientState.clearPendingStack();
            return true;
        }
        ItemStack carried = screen.getMenu().getCarried();
        if (!carried.isEmpty()) {
            FilterClientState.setPendingStack(carried.copyWithCount(1));
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        narration.add(NarratedElementType.TITLE, Component.literal("放入物品进行标记"));
        narration.add(NarratedElementType.USAGE, Component.literal("放入物品后,#选择标签标记,+直接标记物品"));
    }

    @Override
    public void visible(boolean visible) {
        this.visible = visible;
    }
}
