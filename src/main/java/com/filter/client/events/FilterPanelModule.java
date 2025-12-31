package com.filter.client.events;

import com.filter.FilterNetwork;
import com.filter.client.component.FilterPanel;
import com.filter.client.component.FilterSwitch;
import com.filter.common.utils.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * 标记面板模块
 *
 * @author cshisan
 */
final class FilterPanelModule implements FilterEventModule {
    private FilterSwitch switchBtn;
    private FilterPanel panel;

    @Override
    public void onInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen inventory) {
            Minecraft minecraft = Minecraft.getInstance();
            this.panel = new FilterPanel(inventory);
            this.switchBtn = new FilterSwitch(
                    inventory, () -> this.panel.visible,
                    () -> this.panel.visible(!this.panel.visible, MouseHelper.guiX(), MouseHelper.guiY())
            );
            event.addListener(this.panel);
            event.addListener(this.switchBtn);
            this.panel.visible(false, MouseHelper.guiX(), MouseHelper.guiY());

            if (minecraft.player != null) {
                // 打开物品栏时向服务端请求最新标记清单
                FilterNetwork.requestSyncOnClient();
            }
        }
    }

    @Override
    public void onRenderPre(ScreenEvent.Render.Pre event) {
        if (this.panel != null && event.getScreen() instanceof InventoryScreen inventory) {
            this.panel.refresh(inventory);
            this.switchBtn.refresh(inventory);
        }
    }

    @Override
    public void onMouseReleasedPre(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getScreen() instanceof InventoryScreen) {
            if (panel != null && panel.isPanelArea(event.getMouseX(), event.getMouseY())) {
                event.setCanceled(true);
            }
        }
    }
}
