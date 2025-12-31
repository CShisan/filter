package com.filter.client.component;

import com.filter.FilterMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
public final class FilterBackground extends AbstractWidget implements FilterComponent {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = ResourceLocation.fromNamespaceAndPath(FilterMod.MOD_ID, "textures/gui/background.png");
    public static final int PADDING = 10;

    FilterBackground(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("标记面板"));
        this.active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RECIPE_BOOK_LOCATION, getX(), getY(), 1, 1, width, height);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
    }

    @Override
    public void visible(boolean visible) {
        this.visible = visible;
    }
}
