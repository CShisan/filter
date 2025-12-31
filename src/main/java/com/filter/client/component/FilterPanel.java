package com.filter.client.component;

import com.filter.client.FilterClientCommands;
import com.filter.client.FilterClientState;
import com.filter.common.utils.MouseHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author cshisan
 */
public class FilterPanel extends AbstractWidget {
    public static final int WIDTH = 147;
    public static final int HEIGHT = 166;
    public static final int GAP = 1;
    private static final int TAG_PANEL_HEIGHT = 80;
    private final FilterBackground background;
    private FilterMark mark;
    private FilterTags tags;
    private final FilterSlot slot;
    private final Button addBtn;
    private final Button tagBtn;
    private final InventoryScreen inventory;
    private long inventoryXy;

    public FilterPanel(InventoryScreen inventory) {
        super(inventory.getGuiLeft() - WIDTH - GAP, inventory.getGuiTop(), WIDTH, HEIGHT, Component.literal("标记"));
        this.inventory = inventory;
        this.inventoryXy = FilterComponent.calc(inventory.getGuiLeft(), inventory.getGuiTop());
        this.background = new FilterBackground(this.getX(), this.getY(), WIDTH, HEIGHT);

        int slotY = this.getY() + HEIGHT - FilterBackground.PADDING - FilterSlot.HEIGHT;
        this.slot = new FilterSlot(inventory, this.getX() + FilterBackground.PADDING, slotY);

        this.mark = this.createMark(slotY, this.getX(), this.getY());
        this.tags = this.createTags(slotY, this.getX());

        this.addBtn = Button.builder(Component.literal("+"), button -> {
            ItemStack pending = FilterClientState.getPendingStack();
            if (!pending.isEmpty()) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(pending.getItem());
                FilterClientCommands.mark(id, false);
                FilterClientState.addMeta(id, false);
            }
        }).bounds(this.getX() + WIDTH - FilterBackground.PADDING - 18, slotY, 18, 18).build();

        this.tagBtn = Button.builder(Component.literal("#"), button -> {
            ResourceLocation tagId = this.tags.selected();
            if (tagId != null) {
                FilterClientCommands.mark(tagId, true);
                FilterClientState.addMeta(tagId, true);
                this.visible(this.visible, MouseHelper.guiX(), MouseHelper.guiY());
            }
        }).bounds(this.addBtn.getX() - 20, slotY, 18, 18).build();
    }

    public boolean isPanelArea(double mouseX, double mouseY) {
        if (FilterComponent.isInArea(this.background, mouseX, mouseY)) {
            return true;
        }
        return this.tags.visible && this.tags.isMouseOver(mouseX, mouseY);
    }

    public void visible(boolean visible, double mouseX, double mouseY) {
        this.visible = visible;
        FilterComponent.visible(this.visible, this.background, this.mark, this.slot);
        this.addBtn.visible = this.tagBtn.visible = this.visible;
        this.tags.visible(this.visible && FilterComponent.isInArea(this.tagBtn, mouseX, mouseY));
    }

    public void refresh(InventoryScreen inventory) {
        int inventoryX = inventory.getGuiLeft();
        int inventoryY = inventory.getGuiTop();
        long current = FilterComponent.calc(inventoryX, inventoryY);
        if (this.inventoryXy != current) {
            this.inventoryXy = current;

            int panelX = inventoryX - WIDTH - GAP;
            FilterComponent.refresh(this, panelX, inventoryY);
            FilterComponent.refresh(this.background, panelX, inventoryY);

            int slotY = inventoryY + HEIGHT - FilterBackground.PADDING - FilterSlot.HEIGHT;
            FilterComponent.refresh(this.slot, panelX + FilterBackground.PADDING, slotY);

            this.mark = this.createMark(slotY, panelX, inventoryY);
            this.tags = this.createTags(slotY, panelX);

            FilterComponent.refresh(this.addBtn, panelX + WIDTH - FilterBackground.PADDING - 18, slotY);
            FilterComponent.refresh(this.tagBtn, this.addBtn.getX() - 20, slotY);
        }

        this.visible(this.visible, MouseHelper.guiX(), MouseHelper.guiY());
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            this.background.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            this.slot.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            this.mark.render(guiGraphics, mouseX, mouseY, partialTick);
            this.tags.render(guiGraphics, mouseX, mouseY, partialTick);
            this.addBtn.render(guiGraphics, mouseX, mouseY, partialTick);
            this.tagBtn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private FilterMark createMark(int slotY, int panelX, int panelY) {
        return new FilterMark(
                WIDTH - FilterBackground.PADDING * 2,
                slotY - panelY - FilterBackground.PADDING - GAP,
                panelY + FilterBackground.PADDING - 2,
                panelX + FilterBackground.PADDING - 2
        );
    }

    private FilterTags createTags(int slotY, int panelX) {
        return new FilterTags(
                WIDTH - FilterBackground.PADDING * 2,
                TAG_PANEL_HEIGHT,
                slotY - GAP - TAG_PANEL_HEIGHT,
                panelX + FilterBackground.PADDING
        );
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        if (this.visible) {
            this.background.updateWidgetNarration(narrationElementOutput);
            this.slot.updateWidgetNarration(narrationElementOutput);
            this.addBtn.updateWidgetNarration(narrationElementOutput);
            this.tagBtn.updateWidgetNarration(narrationElementOutput);
            this.mark.updateNarration(narrationElementOutput);
            this.tags.updateNarration(narrationElementOutput);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        if (this.tags.visible && this.tags.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.addBtn.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.tagBtn.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.slot.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return this.mark.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.visible) {
            if (FilterComponent.isInArea(this.slot, mouseX, mouseY)) {
                ItemStack carried = this.inventory.getMenu().getCarried();
                if (!carried.isEmpty()) {
                    FilterClientState.setPendingStack(carried.copyWithCount(1));
                }
            }

            boolean handled = false;
            if (this.tags.visible) {
                handled |= this.tags.mouseReleased(mouseX, mouseY, button);
            }
            handled |= this.addBtn.mouseReleased(mouseX, mouseY, button);
            handled |= this.tagBtn.mouseReleased(mouseX, mouseY, button);
            handled |= this.slot.mouseReleased(mouseX, mouseY, button);
            handled |= this.mark.mouseReleased(mouseX, mouseY, button);
            return handled;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.visible) {
            return false;
        }
        boolean handled = false;
        if (this.tags.visible) {
            handled |= this.tags.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        handled |= this.mark.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.visible) {
            return false;
        }

        if (FilterComponent.isInArea(this.tagBtn, mouseX, mouseY)) {
            if (!FilterClientState.getPendingStack().isEmpty()) {
                int step = scrollY > 0 ? -1 : 1;
                this.tags.scroll(step);
                return true;
            }
        }

        if (this.tags.visible && this.tags.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        return this.mark.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
