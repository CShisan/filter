package com.filter;

import com.filter.client.model.MarkMeta;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

final class FilterPlayerData {
    private static final String ROOT_KEY = "filter";
    private static final String METAS_KEY = "marked_metas";
    private static final String META_TAG_KEY = "tag";
    private static final String META_ID_KEY = "id";

    static void mark(ServerPlayer player, ResourceLocation id, boolean isTag) {
        List<MarkMeta> metas = read(player);
        if (isMarked(metas, id)) {
            return;
        }
        metas.add(MarkMeta.create(isTag, id));
        write(player, metas);
    }

    static void unmark(ServerPlayer player, ResourceLocation id, boolean isTag) {
        List<MarkMeta> metas = read(player);
        if (!isMarked(metas, id)) {
            return;
        }
        metas.removeIf(meta -> meta.tag() == isTag && meta.id().equals(id));
        write(player, metas);
    }

    static boolean isMarked(ServerPlayer player, ResourceLocation id) {
        return isMarked(read(player), id);
    }

    private static boolean isMarked(List<MarkMeta> metas, ResourceLocation id) {
        return metas.stream().anyMatch(meta -> meta.id().equals(id));
    }

    static List<MarkMeta> read(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag root = data.contains(ROOT_KEY, Tag.TAG_COMPOUND) ? data.getCompound(ROOT_KEY) : new CompoundTag();
        ListTag list = root.getList(METAS_KEY, Tag.TAG_COMPOUND);
        List<MarkMeta> metas = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            boolean isTag = entry.getBoolean(META_TAG_KEY);
            String idText = entry.getString(META_ID_KEY);
            ResourceLocation id = ResourceLocation.tryParse(idText);
            if (id != null) {
                metas.add(new MarkMeta(isTag, id, List.of()));
            }
        }
        return metas;
    }

    private static void write(ServerPlayer player, List<MarkMeta> metas) {
        ListTag list = new ListTag();
        for (MarkMeta meta : metas) {
            CompoundTag entry = new CompoundTag();
            entry.putBoolean(META_TAG_KEY, meta.tag());
            entry.putString(META_ID_KEY, meta.id().toString());
            list.add(entry);
        }

        CompoundTag data = player.getPersistentData();
        CompoundTag root = data.contains(ROOT_KEY, Tag.TAG_COMPOUND) ? data.getCompound(ROOT_KEY) : new CompoundTag();
        root.put(METAS_KEY, list);
        data.put(ROOT_KEY, root);
    }
}
