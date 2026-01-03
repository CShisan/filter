package com.filter.client.component;

import com.filter.client.FilterClientState;
import com.filter.common.utils.TagHelper;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
final class FilterTags extends ScrollPanel implements FilterComponent, FilterScrollText {
    private static final int LINE_HEIGHT = 16;
    private static final int BG_COLOR = 0xE6000000;
    private final List<ResourceLocation> tags = new ArrayList<>();
    public boolean visible = false;
    private int selectedIndex = 0;

    FilterTags(int width, int height, int top, int left) {
        super(Minecraft.getInstance(), width, height, top, left);
    }

    void setTags(List<ResourceLocation> tagList) {
        tags.clear();
        tags.addAll(tagList);
        this.selectedIndex = 0;
    }

    ResourceLocation selected() {
        if (tags.isEmpty()) {
            return null;
        }
        return tags.get(Math.max(0, Math.min(tags.size() - 1, selectedIndex)));
    }

    void scroll(int delta) {
        if (!tags.isEmpty()) {
            selectedIndex = Math.max(0, Math.min(tags.size() - 1, selectedIndex + delta));
            this.ensureSelectedVisible();
        }
    }

    /**
     * 确保所选行可见
     */
    private void ensureSelectedVisible() {
        int viewHeight = this.height - this.border;
        int rowTop = this.selectedIndex * LINE_HEIGHT;
        int rowBottom = rowTop + LINE_HEIGHT;
        if (rowTop < this.scrollDistance) {
            this.scrollDistance = rowTop;
        } else if (rowBottom > this.scrollDistance + viewHeight) {
            this.scrollDistance = rowBottom - viewHeight;
        }
        int maxScroll = this.getContentHeight() - viewHeight;
        if (maxScroll < 0) {
            maxScroll = 0;
        }
        if (this.scrollDistance < 0) {
            this.scrollDistance = 0;
        }
        if (this.scrollDistance > maxScroll) {
            this.scrollDistance = maxScroll;
        }
    }

    @Override
    protected int getContentHeight() {
        int lines = tags.size();
        return Math.max(1, lines) * LINE_HEIGHT + 4;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) {
            return;
        }
        guiGraphics.pose().pushPose();
        // 标签选择层需覆盖在滚动列表之上
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.enableScissor(this.left, this.top, this.left + this.width, this.top + this.height);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }

    @Override
    protected void drawPanel(@NotNull GuiGraphics guiGraphics, int entryRight, int relativeY, @NotNull Tesselator tess, int mouseX, int mouseY) {
        int y = relativeY;
        for (int index = 0; index < tags.size(); index++) {
            int color = index == selectedIndex ? 0xFFFF55 : 0xE0E0E0;
            ResourceLocation id = tags.get(index);
            FilterScrollText.draw(
                    guiGraphics, "#" + id,
                    this.left + 4, y + 4, y, color,
                    this.left + this.width - 4, LINE_HEIGHT
            );
            y += LINE_HEIGHT;
        }
        if (tags.isEmpty()) {
            guiGraphics.drawString(FONT, "无可用标签", this.left + 4, y + 4, 0xB0B0B0, false);
        }
    }

    @Override
    protected void drawBackground(@NotNull GuiGraphics guiGraphics, @NotNull Tesselator tess, float partialTick) {
        guiGraphics.fill(this.left, this.top, this.left + this.width, this.top + this.height, BG_COLOR);
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        if (visible && this.getFocused() != null) {
            return NarrationPriority.FOCUSED;
        }
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narration) {
        narration.add(NarratedElementType.USAGE, Component.literal("滚动标签进行选择"));
    }

    @Override
    public void visible(boolean visible) {
        ItemStack stack = FilterClientState.getPendingStack();
        this.visible = visible && !stack.isEmpty();
        if (this.visible) {
            this.ensureSelectedVisible();
            if (this.tags.isEmpty()) {
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
                this.setTags(TagHelper.item2tags(key));
            }
        } else {
            this.tags.clear();
            this.selectedIndex = 0;
        }
    }
}
