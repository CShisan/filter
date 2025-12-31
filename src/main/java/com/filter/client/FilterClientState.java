package com.filter.client;

import com.filter.client.model.MarkMeta;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
public final class FilterClientState {
    private static final List<MarkMeta> METAS = new ArrayList<>();
    private static ItemStack pendingStack = ItemStack.EMPTY;

    public static List<MarkMeta> metas() {
        return List.copyOf(METAS);
    }

    public static void addMeta(ResourceLocation id, boolean isTag) {
        if (METAS.stream().noneMatch(meta -> meta.id().equals(id))) {
            METAS.add(MarkMeta.create(isTag, id));
        }
        FilterClientState.clearPendingStack();
    }

    public static void removeMeta(ResourceLocation id) {
        METAS.removeIf(meta -> meta.id().equals(id));
    }

    public static void setMetas(List<MarkMeta> metas) {
        METAS.clear();
        METAS.addAll(metas);
    }

    public static ItemStack getPendingStack() {
        return pendingStack;
    }

    public static void setPendingStack(ItemStack stack) {
        pendingStack = stack;
    }

    public static void clearPendingStack() {
        pendingStack = ItemStack.EMPTY;
    }
}
