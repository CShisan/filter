package com.filter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

final class FilterPickupHandler {
    static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        ItemStack stack = event.getItemEntity().getItem();
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        if (!stack.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (FilterPlayerData.isMarked(player, id)) {
                event.setCanPickup(TriState.FALSE);
            }
        }
    }
}
