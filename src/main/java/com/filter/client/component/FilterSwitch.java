package com.filter.client.component;

import com.filter.FilterMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
public final class FilterSwitch extends AbstractWidget implements FilterComponent {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FilterMod.MOD_ID, "textures/gui/switch.png");
    private static final int ICON_SIZE = 16;
    private static final int DISPLAY_SIZE = 10;
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 16;
    private static final float SCALE = DISPLAY_SIZE / (float) ICON_SIZE;
    public static final int RELATIVE_X = 5;
    public static final int RELATIVE_Y = 5;
    private final Runnable onPress;
    private final BooleanSupplier panelActivated;

    public FilterSwitch(InventoryScreen inventory, BooleanSupplier active, Runnable onPress) {
        super(
                FilterSwitch.calcX(inventory), FilterSwitch.calcY(inventory),
                DISPLAY_SIZE, DISPLAY_SIZE, Component.literal("标记")
        );
        this.onPress = onPress;
        this.panelActivated = active;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int hOffset = this.panelActivated.getAsBoolean() ? ICON_SIZE : 0;
        // 以 10x10 显示 16x16 的雪碧图帧
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().scale(SCALE, SCALE, 1.0f);
        guiGraphics.blit(TEXTURE, 0, 0, hOffset, 0, ICON_SIZE, ICON_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        narration.add(NarratedElementType.USAGE, Component.literal("点击打开标记面板"));
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        onPress.run();
    }

    @Override
    public void visible(boolean visible) {
        this.visible = visible;
    }

    public void refresh(InventoryScreen inventory) {
        int curX = FilterSwitch.calcX(inventory);
        int curY = FilterSwitch.calcY(inventory);
        if (this.getX() != curX || this.getY() != curY) {
            FilterComponent.refresh(this, curX, curY);
        }
    }

    public static int calcX(InventoryScreen inventory) {
        return inventory.getGuiLeft() + inventory.getXSize() - DISPLAY_SIZE - FilterSwitch.RELATIVE_X;
    }

    public static int calcY(InventoryScreen inventory) {
        return inventory.getGuiTop() + FilterSwitch.RELATIVE_Y;
    }
}
